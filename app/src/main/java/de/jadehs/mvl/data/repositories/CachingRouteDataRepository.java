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

    private Single<Parking[]> cachedParking;
    private Single<List<Route>> cachedRoutes;
    private final Map<Long, Single<Route>> cachedRoute;
    private CachedEntry<Single<ParkingDailyStats[]>> cachedDailyStats;
    private CachedEntry<Single<ParkingCurrOccupancy[]>> cachedCurrOccupancies;


    private final long cacheTime;


    public CachingRouteDataRepository(RouteDataRepository parent) {
        // 10 min
        this(parent, 10 * 60 * 1000);
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
    public synchronized Single<Parking[]> getAllParking() {
        if (cachedParking == null) {
            return super.getAllParking().cache();
        }
        return this.cachedParking;
    }

    @Override
    public synchronized Single<List<Route>> getAllRoutes() {
        if (this.cachedRoutes == null) {
            this.cachedRoutes = super.getAllRoutes().cache();
        }
        return this.cachedRoutes;
    }

    @Override
    public synchronized Single<Route> getRoute(long id) {
        Single<Route> cached = this.cachedRoute.get(id);
        if (cached == null) {
            cached = super.getRoute(id).cache();
            this.cachedRoute.put(id, cached);
        }
        return cached;
    }

    @Override
    public synchronized Single<ParkingDailyStats[]> getAllParkingDailyStats() {
        if (cachedDailyStats == null || cachedDailyStats.isExpired(this.cacheTime)) {
            cachedDailyStats = new CachedEntry<>(super.getAllParkingDailyStats().cache());
        }

        return cachedDailyStats.getEntry();
    }

    @Override
    public synchronized Single<ParkingCurrOccupancy[]> getAllOccupancies() {
        if (cachedCurrOccupancies == null || cachedCurrOccupancies.isExpired(this.cacheTime)) {
            cachedCurrOccupancies = new CachedEntry<>(super.getAllOccupancies().cache());
        }
        return cachedCurrOccupancies.getEntry();
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

    private static class CachedEntry<T> {
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
