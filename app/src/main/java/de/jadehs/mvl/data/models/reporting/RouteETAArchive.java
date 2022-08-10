package de.jadehs.mvl.data.models.reporting;

import java.io.File;
import java.util.Locale;

import de.jadehs.mvl.data.models.routing.CurrentRouteETA;
import de.jadehs.mvl.data.models.routing.CurrentRouteETAReport;

/**
 * Stores all given {@link CurrentRouteETA} instances.
 */
public class RouteETAArchive extends JSONArchive<CurrentRouteETAReport> {
    private static final String TAG = "RouteETAArchive";

    private final long routeId;

    public RouteETAArchive(File dir, long routeId) {
        super(new File(dir, String.format(Locale.ROOT, "archivedETAS%d.raw", routeId)));
        this.routeId = routeId;
    }

    public boolean add(CurrentRouteETAReport routeETA) {
        if (routeETA.getETA().getRoute().getId() != this.routeId) {
            throw new IllegalArgumentException("given routeETA route id does not match the routeId of this archive");
        }

        return super.add(routeETA);
    }
}
