package de.jadehs.mvl.data.models.reporting;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.jadehs.mvl.data.models.JsonSerializable;

public abstract class JSONArchive<T extends JsonSerializable> {
    private static final String TAG = "JSONArchive";

    private static final byte[] SEPARATOR = ",\n".getBytes(StandardCharsets.UTF_8);

    @NonNull
    private final File archiveFile;
    @NonNull
    private final List<String> remainingData = Collections.synchronizedList(new LinkedList<>());

    private final Object fileLock = new Object();

    public JSONArchive(@NonNull File archiveFile) {
        this.archiveFile = archiveFile;
        if (!archiveFile.getParentFile().exists()) {
            archiveFile.mkdirs();
        }
    }


    public boolean add(T dataObject) {
        try {
            String data = dataObject.toJson().toString();
            remainingData.add(data);
            appendRemaining();
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Failed to save a given route to storage", e);
            return false;
        }
        return true;


    }

    private List<String> toETAList() {
        List<String> allETAs = new LinkedList<>();
        synchronized (fileLock) {
            if (!this.archiveFile.exists()) {
                return allETAs;
            }
            try (InputStream stream = new FileInputStream(this.archiveFile);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    allETAs.add(line);
                }
                if (!allETAs.isEmpty()) {
                    int lastIndex = allETAs.size() - 1;
                    line = allETAs.remove(lastIndex);
                    allETAs.add(line.substring(0, line.length() - 1));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return allETAs;
    }

    /**
     * reads all data from disk and creates a JSON parsable string
     * which contains all routeETAs in an array
     *
     * @return
     */
    public String toJSONString() {
        StringWriter stringWriter = new StringWriter();
        try {
            writeTo(stringWriter);
        } catch (IOException unlikely) {
            // StringWriter doesn't throw an exception so, there shouldn't be one
            throw new IllegalStateException(unlikely);
        }
        return stringWriter.toString();
    }

    public void writeTo(Writer writer) throws IOException {

        List<String> allETAs = toETAList();

        Iterator<String> etaIterator = allETAs.iterator();
        writer.write("[");
        synchronized (remainingData) {
            Iterator<String> remainingIterator = remainingData.iterator();
            while (etaIterator.hasNext() || remainingIterator.hasNext()) {
                boolean isAtLeastSecondOfRemaining = remainingIterator.hasNext() && !etaIterator.hasNext();

                writer.append(etaIterator.hasNext() ? etaIterator.next() : remainingIterator.next());
                writer.append("\n");

                if (isAtLeastSecondOfRemaining && remainingIterator.hasNext()) {
                    writer.append(new String(SEPARATOR));
                }
            }
        }

        writer.append("]");
    }

    /**
     * tries writing all data contained in the remainingData list to disk,
     * any data that couldn't get written remains in the remainingData list
     *
     * @throws IOException if anything goes wrong
     */
    private void appendRemaining() throws IOException {
        synchronized (fileLock) {
            synchronized (remainingData) {
                try (OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(this.archiveFile, true))) {
                    Iterator<String> remainingData = this.remainingData.iterator();
                    while (remainingData.hasNext()) {
                        byte[] allData = remainingData.next().getBytes(StandardCharsets.UTF_8);
                        fileStream.write(allData);
                        fileStream.write(SEPARATOR);
                        remainingData.remove();
                    }
                }
            }
        }

    }

    /**
     * Deletes all entries this archive currently manages
     */
    public void clear() {
        synchronized (fileLock) {
            this.archiveFile.delete();
            this.remainingData.clear();
        }
    }

    /**
     * saves the current contents to the given file
     *
     * @param destination
     * @return
     */
    public boolean backup(File destination) {

        if (!destination.isFile()) {
            return false;
        }
        if (!destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }
        boolean done = true;
        try (Writer writer = new BufferedWriter(new FileWriter(destination));) {
            synchronized (fileLock) {
                writeTo(writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            done = false;
        }
        return done;
    }
}
