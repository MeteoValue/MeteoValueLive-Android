package de.jadehs.mvl.data.models;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import de.jadehs.mvl.data.models.parking.ParkingOccupancyReport;
import de.jadehs.mvl.data.models.reporting.ETAParkingArchive;
import de.jadehs.mvl.data.models.reporting.LocationArchive;
import de.jadehs.mvl.data.models.reporting.LocationReport;
import de.jadehs.mvl.data.models.reporting.ParkingOccupancyReportArchive;
import de.jadehs.mvl.data.models.reporting.RouteETAArchive;
import de.jadehs.mvl.data.models.routing.CurrentRouteETA;
import de.jadehs.mvl.data.models.routing.CurrentRouteETAReport;

public interface ReportArchive {

    static ReportArchive fromContext(Context context, long routeId) {
        return fromContext(context, new ParkingOccupancyReportArchive(context.getFilesDir()), routeId);
    }


    static ReportArchive fromContext(Context context,
                                     ParkingOccupancyReportArchive parkingArchive,
                                     long routeId) {

        return new ETAParkingArchive(
                new File(context.getCacheDir(), "reports"),
                new File(context.getCacheDir(), "reportBackups"),
                parkingArchive,
                new RouteETAArchive(context.getFilesDir(), routeId),
                new LocationArchive(context.getFilesDir())
        );
    }


    /**
     * Add a {@link CurrentRouteETA} to the archive
     *
     * @param routeETA the eta to save
     */
    void addRouteETA(CurrentRouteETAReport routeETA);

    /**
     * Add a {@link ParkingOccupancyReport} to the archive
     *
     * @param parkingReport the report to save
     */
    void addParkingReport(ParkingOccupancyReport parkingReport);

    /**
     * Add a {@link LocationReport} to the archive
     *
     * @param location the location to save
     */
    void addLocation(LocationReport location);

    /**
     * Writes a file synchronously which contains all data currently saved in this archive
     *
     * @return a file with all data contained in this archive
     */
    File writePublishFile() throws IOException;


    void clearArchive();

}
