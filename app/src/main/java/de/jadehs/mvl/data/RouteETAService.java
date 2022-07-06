package de.jadehs.mvl.data;

import androidx.annotation.NonNull;

import de.jadehs.mvl.data.models.routing.RouteETA;
import de.jadehs.mvl.data.remote.routing.RouteRequest;
import io.reactivex.rxjava3.core.Single;

public interface RouteETAService {

    /**
     * Single does resolve to a {@link RouteETA} instance, calculated from the given RouteRequest
     * @param request a request which describes the route
     * @return a single instance which retrieves the data asynchronously
     */
    Single<RouteETA> createRouteETA(@NonNull RouteRequest request);

    /**
     * Single does resolve to a {@link RouteETA} instance, specified by the given id.
     *
     * ETA values are updated
     * @param id id of the RouteETA returned by createRoute
     * @return a single instance which retrieves the data asynchronously
     */
    Single<RouteETA> getETA(long id);
}
