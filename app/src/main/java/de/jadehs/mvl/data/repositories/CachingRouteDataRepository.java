package de.jadehs.mvl.data.repositories;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.jadehs.mvl.data.RouteDataRepository;
import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;
import de.jadehs.mvl.data.models.parking.ParkingDailyStats;
import de.jadehs.mvl.data.models.routing.Route;
import io.reactivex.rxjava3.core.Single;

/**
 * decorator which caches the getAllParking, getAllRoutes and getRoute functions
 */
public class CachingRouteDataRepository extends DecoratorRouteDataRepository {

    private Parking[] cachedParking;
    private List<Route> cachedRoutes;
    private final Map<Long, Route> cachedRoute;
    private CachedEntry<ParkingDailyStats[]> cachedDailyStats;
    private CachedEntry<ParkingCurrOccupancy[]> cachedCurrOccupancies;


    // 30s
    private final long cacheTime;


    public CachingRouteDataRepository(RouteDataRepository parent) {
        this(parent, 30 * 1000);
    }

    public CachingRouteDataRepository(RouteDataRepository parent, long cacheTime) {
        super(parent);
        this.cachedRoute = new ConcurrentHashMap<>();
        this.cacheTime = cacheTime;
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

    @Override
    public Single<ParkingDailyStats[]> getAllParkingDailyStats() {
        if (cachedDailyStats != null && !cachedDailyStats.isExpired(this.cacheTime)) {
            return Single.just(this.cachedDailyStats.getEntry());
        }
        return super.getAllParkingDailyStats();
    }

    @Override
    public Single<ParkingCurrOccupancy[]> getAllOccupancies() {
        if (cachedCurrOccupancies != null && !cachedCurrOccupancies.isExpired(this.cacheTime)) {
            return Single.just(this.cachedCurrOccupancies.getEntry());
        }
        return super.getAllOccupancies();
    }

    /**
     * Clears all cached this Repository populated
     */
    public void clearCache() {
        this.cachedParking = null;
        this.cachedRoutes = null;
        this.cachedRoute.clear();
        this.cachedDailyStats = null;
        this.cachedCurrOccupancies = null;
    }

    private class CachedEntry<T> {
        private final long cachedTime;
        private final T entry;

        public CachedEntry(T entry) {
            this(entry, System.currentTimeMillis());
        }

        public CachedEntry(T entry, long cachedTime) {
            this.entry = entry;
            this.cachedTime = cachedTime;
        }

        public long getCachedTime() {
            return cachedTime;
        }

        public T getEntry() {
            return entry;
        }

        /**
         * 0
         *
         * @param validTime
         * @return
         */
        public boolean isExpired(long validTime) {
            return System.currentTimeMillis() - this.getCachedTime() > validTime;
        }
    }
}
