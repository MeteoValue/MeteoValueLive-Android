package de.jadehs.mvl.data.models.reporting;

import androidx.annotation.NonNull;

import java.io.File;

import de.jadehs.mvl.data.models.parking.ParkingOccupancyReport;

public class ParkingOccupancyReportArchive extends JSONArchive<ParkingOccupancyReport> {

    public ParkingOccupancyReportArchive(@NonNull File archiveFolder) {
        super(new File(archiveFolder, "parkingArchive.raw"));
    }
}
