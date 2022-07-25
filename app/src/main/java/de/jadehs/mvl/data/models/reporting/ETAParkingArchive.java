package de.jadehs.mvl.data.models.reporting;

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

public class ETAParkingArchive implements ReportArchive {

    @NonNull
    private final ParkingOccupancyReportArchive parkingOccupancyReportArchive;
    @NonNull
    private final RouteETAArchive etaArchive;
    @NonNull
    private final File dataFolder;

    public ETAParkingArchive(@NonNull File dataFolder, @NonNull ParkingOccupancyReportArchive parkingOccupancyReportArchive, @NonNull RouteETAArchive etaArchive) {
        this.dataFolder = dataFolder;
        this.parkingOccupancyReportArchive = parkingOccupancyReportArchive;
        this.etaArchive = etaArchive;
    }

    @Override
    public void addRouteETA(CurrentRouteETA routeETA) {
        this.etaArchive.add(routeETA);
    }

    @Override
    public void addParkingReport(ParkingOccupancyReport parkingReport) {
        this.parkingOccupancyReportArchive.add(parkingReport);
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
        }

        return zipTempFile;
    }

    @Override
    public void clearArchive() {
        parkingOccupancyReportArchive.clear();
        etaArchive.clear();
    }
}
