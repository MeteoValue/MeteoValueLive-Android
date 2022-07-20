package de.jadehs.mvl.data.models.reporting;

import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.jadehs.mvl.data.models.routing.CurrentRouteETA;

/**
 * Stores all given {@link CurrentRouteETA} instances.
 */
public class RouteETAArchive {
    private static final String TAG = "RouteETAArchive";
    private static final byte[] ETA_SEPARATOR = "\n".getBytes(StandardCharsets.UTF_8);

    @NonNull
    private final File archiveFile;
    private final long routeId;

    private final List<String> remainingData = new LinkedList<>();

    public RouteETAArchive(File dir, long routeId) {
        this.routeId = routeId;
        archiveFile = new File(dir, String.format(Locale.ROOT, "archivedETAS%d.json", routeId));
        if (!archiveFile.getParentFile().exists()) {
            archiveFile.mkdirs();
        } else if (archiveFile.exists()) {
            archiveFile.delete();
        }
    }

    public boolean add(CurrentRouteETA routeETA) {
        if (routeETA.getRoute().getId() != this.routeId) {
            throw new IllegalArgumentException("given routeETA route id does not match the routeId of this archive");
        }

        try {
            String data = routeETA.toJson().toString();
            remainingData.add(data);
            appendRemaining();

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Failed to save a given route to storage", e);
            return false;
        }
        return true;


    }

    /**
     * reads all data from disk and creates a JSON parsable string
     * which contains all routeETAs in an array
     *
     * @return
     */
    public String toArrayString() {
        List<String> allETAs = new LinkedList<>();

        try (InputStream stream = new FileInputStream(this.archiveFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allETAs.add(new String(Base64.decode(line, Base64.DEFAULT), StandardCharsets.UTF_8));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder jsonStringBuilder = new StringBuilder("[");
        Iterator<String> etaIterator = allETAs.iterator();
        Iterator<String> remainingIterator = remainingData.iterator();
        while (etaIterator.hasNext() || remainingIterator.hasNext()) {
            jsonStringBuilder.append(etaIterator.hasNext() ? etaIterator.next() : remainingIterator.next());
            if (etaIterator.hasNext() || remainingIterator.hasNext()) {
                jsonStringBuilder.append(",");
            }
        }
        jsonStringBuilder.append("]");
        return jsonStringBuilder.toString();
    }

    /**
     * tries writing all data contained in the remainingData list to disk,
     * any data that couldn't get written remains in the remainingData list
     *
     * @throws IOException if anything goes wrong
     */
    private void appendRemaining() throws IOException {
        try (OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(this.archiveFile, true))) {
            Iterator<String> remainingData = this.remainingData.iterator();
            while (remainingData.hasNext()) {
                byte[] allData = remainingData.next().getBytes(StandardCharsets.UTF_8);
                byte[] allDataEncoded = Base64.encode(allData, Base64.NO_WRAP);
                fileStream.write(allDataEncoded);
                fileStream.write(ETA_SEPARATOR);
                remainingData.remove();
            }
        }
    }
}
