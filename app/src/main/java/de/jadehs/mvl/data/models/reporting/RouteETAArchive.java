package de.jadehs.mvl.data.models.reporting;

import java.io.File;
import java.util.Locale;

import de.jadehs.mvl.data.models.routing.CurrentRouteETA;

/**
 * Stores all given {@link CurrentRouteETA} instances.
 */
public class RouteETAArchive extends JSONArchive<CurrentRouteETA> {
    private static final String TAG = "RouteETAArchive";

    private final long routeId;

    public RouteETAArchive(File dir, long routeId, boolean overrideFile) {
        super(new File(dir, String.format(Locale.ROOT, "archivedETAS%d.raw", routeId)), overrideFile);
        this.routeId = routeId;
    }

    public boolean add(CurrentRouteETA routeETA) {
        if (routeETA.getRoute().getId() != this.routeId) {
            throw new IllegalArgumentException("given routeETA route id does not match the routeId of this archive");
        }

        return super.add(routeETA);
    }
}
