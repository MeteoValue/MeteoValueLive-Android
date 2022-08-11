package de.jadehs.mvl.data.models.reporting;

import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.jadehs.mvl.R;
import de.jadehs.mvl.data.models.ReportArchive;
import de.jadehs.mvl.data.models.parking.ParkingOccupancyReport;
import de.jadehs.mvl.data.models.routing.CurrentRouteETAReport;
import de.jadehs.mvl.provider.ReportsFileProvider;

public class ETAParkingArchive implements ReportArchive {

    private static final String TAG = "ETAParkingArchive";


    public static Intent getEmailIntent(File reportsFile, Context context) {
        Uri reportsUri = FileProvider.getUriForFile(
                context,
                ReportsFileProvider.AUTHORITY,
                reportsFile
        );

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        {
            emailIntent.putExtra(Intent.EXTRA_STREAM, reportsUri);

            emailIntent.putExtra(
                    Intent.EXTRA_EMAIL,
                    new String[]{context.getString(R.string.report_email)}
            );

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reports ");

            emailIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Sehr geehrtes MeteoValueLive-Team,\n" +
                            "im Anhang finden Sie die Parkplatz- und ETA-Berichte die bisher angefallen sind.\n" +
                            "\n" +
                            "Mit freundlichen Grüßen\n" +
                            ""
            );

            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            emailIntent.setClipData(ClipData.newRawUri("Report Data", reportsUri));

            emailIntent.setType("application/zip");
        }
        return emailIntent;
    }

    @NonNull
    private final ParkingOccupancyReportArchive parkingOccupancyReportArchive;
    @NonNull
    private final RouteETAArchive etaArchive;
    @NonNull
    private final LocationArchive locationArchive;
    @NonNull
    private final File dataFolder;
    @NonNull
    private final File backupsFolder;


    public ETAParkingArchive(@NonNull File publishedReportsFolder, @NonNull File backupsFolder, @NonNull ParkingOccupancyReportArchive parkingOccupancyReportArchive, @NonNull RouteETAArchive etaArchive, @NonNull LocationArchive locationArchive) {
        this.dataFolder = publishedReportsFolder;
        this.backupsFolder = backupsFolder;
        this.parkingOccupancyReportArchive = parkingOccupancyReportArchive;
        this.etaArchive = etaArchive;
        this.locationArchive = locationArchive;
    }

    @Override
    public void addRouteETA(CurrentRouteETAReport routeETA) {
        this.etaArchive.add(routeETA);
    }

    @Override
    public void addParkingReport(ParkingOccupancyReport parkingReport) {
        this.parkingOccupancyReportArchive.add(parkingReport);
    }

    public void addLocation(Location location) {
        this.addLocation(LocationReport.fromLocation(location));
    }

    @Override
    public void addLocation(LocationReport location) {
        this.locationArchive.add(location);
    }

    @Override
    public File writePublishFile() throws IOException {

        if (!dataFolder.exists())
            dataFolder.mkdirs();
        File zipTempFile =
                File.createTempFile(
                        "reports",
                        ".zip",
                        dataFolder
                );

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipTempFile)));
             Writer writer = new OutputStreamWriter(zipOutputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry("parkingReports.json"));

            this.parkingOccupancyReportArchive.writeTo(writer);

            writer.flush();

            zipOutputStream.closeEntry();

            zipOutputStream.putNextEntry(new ZipEntry("routeETAs.json"));

            etaArchive.writeTo(writer);

            writer.flush();

            zipOutputStream.closeEntry();

            zipOutputStream.putNextEntry(new ZipEntry("locations.json"));

            locationArchive.writeTo(writer);

            writer.flush();

            zipOutputStream.closeEntry();
        }

        return zipTempFile;
    }

    @Override
    public void clearArchive() {
        long timestamp = System.currentTimeMillis();
        if (parkingOccupancyReportArchive.backup(new File(backupsFolder, "parkingReport" + timestamp + "backup.json")))
            parkingOccupancyReportArchive.clear();

        if (etaArchive.backup(new File(backupsFolder, "etaReport" + timestamp + "backup.json")))
            etaArchive.clear();

        if (locationArchive.backup(new File(backupsFolder, "locationReport" + timestamp + "backup.json")))
            locationArchive.clear();
    }
}
