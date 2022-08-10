package de.jadehs.mvl.data.models.reporting;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.jadehs.mvl.data.models.ReportArchive;
import de.jadehs.mvl.data.models.parking.ParkingOccupancyReport;
import de.jadehs.mvl.data.models.routing.CurrentRouteETA;
import de.jadehs.mvl.data.models.routing.CurrentRouteETAReport;

public class ETAParkingArchive implements ReportArchive {

    private static final String TAG = "ETAParkingArchive";

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
