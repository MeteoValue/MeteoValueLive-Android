package de.jadehs.mvl.data;

import java.util.List;

import de.jadehs.mvl.data.models.routing.Route;
import io.reactivex.rxjava3.core.Single;

public interface RouteManager {


    /**
     * Returns a Single resolving to all available routes
     * @return a single
     */
    Single<List<Route>> getAllRoutes();

    /**
     * Returns a Single resolving to the route by id
     *
     * if the route doesn't exists the single completes exceptionally
     * @return a single
     */
    Single<Route> getRoute(long id);
}
