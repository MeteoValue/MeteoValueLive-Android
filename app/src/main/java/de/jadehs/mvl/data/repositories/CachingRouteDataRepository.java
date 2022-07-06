package de.jadehs.mvl.data.repositories;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.jadehs.mvl.data.RouteDataRepository;
import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.routing.Route;
import io.reactivex.rxjava3.core.Single;

/**
 * decorator which caches the getAllParking, getAllRoutes and getRoute functions
 */
public class CachingRouteDataRepository extends DecoratorRouteDataRepository {

    private Parking[] cachedParking;
    private List<Route> cachedRoutes;
    private final Map<Long, Route> cachedRoute;

    public CachingRouteDataRepository(RouteDataRepository parent) {
        super(parent);
        this.cachedRoute = new ConcurrentHashMap<>();
    }

    /**
     * Caches the first response then returns the cached result
     */
    @Override
    public Single<Parking[]> getAllParking() {
        if (cachedParking != null) {
            return Single.just(this.cachedParking);
        }
        return super.getAllParking().doOnSuccess(parkings -> this.cachedParking = parkings);
    }

    @Override
    public Single<List<Route>> getAllRoutes() {
        if (this.cachedRoutes != null) {
            return Single.just(this.cachedRoutes);
        }
        return super.getAllRoutes().doOnSuccess(routes -> this.cachedRoutes = routes);
    }

    @Override
    public Single<Route> getRoute(long id) {
        Route cached = this.cachedRoute.get(id);
        if (cached != null) {
            return Single.just(cached);
        }
        return super.getRoute(id).doOnSuccess(route -> this.cachedRoute.put(id, route));
    }

    /**
     * Clears all cached this Repository populated
     */
    public void clearCache() {
        this.cachedParking = null;
        this.cachedRoutes = null;
        this.cachedRoute.clear();
    }
}
