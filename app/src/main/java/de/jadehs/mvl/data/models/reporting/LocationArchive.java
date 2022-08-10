package de.jadehs.mvl.data.models.reporting;

import androidx.annotation.NonNull;

import java.io.File;

public class LocationArchive extends JSONArchive<LocationReport> {
    public LocationArchive(@NonNull File archiveFolder) {
        super(new File(archiveFolder, "locationArchive.raw"));
    }
}
