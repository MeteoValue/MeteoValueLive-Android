package de.jadehs.mvl.data.models;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import de.jadehs.mvl.data.models.parking.ParkingOccupancyReport;
import de.jadehs.mvl.data.models.reporting.ETAParkingArchive;
import de.jadehs.mvl.data.models.reporting.ParkingOccupancyReportArchive;
import de.jadehs.mvl.data.models.reporting.RouteETAArchive;
import de.jadehs.mvl.data.models.routing.CurrentRouteETA;
import kotlin.io.FilesKt;

public interface ReportArchive {

    static ReportArchive fromContext(Context context, long routeId) {
        return fromContext(context, new ParkingOccupancyReportArchive(context.getFilesDir()), routeId);
    }


    static ReportArchive fromContext(Context context,
                                     ParkingOccupancyReportArchive parkingArchive,
                                     long routeId) {

        return new ETAParkingArchive(
                FilesKt.resolve(context.getCacheDir(), "reports"),
                parkingArchive,
                new RouteETAArchive(context.getFilesDir(), routeId)
        );
    }


    /**
     * Add a {@link CurrentRouteETA} to the archive
     *
     * @param routeETA the eta to save
     */
    void addRouteETA(CurrentRouteETA routeETA);

    /**
     * Add a {@link ParkingOccupancyReport} to the archive
     *
     * @param parkingReport the report to save
     */
    void addParkingReport(ParkingOccupancyReport parkingReport);

    /**
     * Writes a file synchronously which contains all data currently saved in this archive
     *
     * @return a file with all data contained in this archive
     */
    File writePublishFile() throws IOException;


    void clearArchive();

}
