package de.jadehs.mvl.data.repositories;

import androidx.annotation.NonNull;

import java.util.List;

import de.jadehs.mvl.data.RouteDataRepository;
import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;
import de.jadehs.mvl.data.models.parking.ParkingDailyStats;
import de.jadehs.mvl.data.models.routing.Route;
import de.jadehs.mvl.data.models.routing.RouteETA;
import de.jadehs.mvl.data.remote.routing.RouteRequest;
import io.reactivex.rxjava3.core.Single;

/**
 * Decorator pattern
 */
public class DecoratorRouteDataRepository implements RouteDataRepository {

    private final RouteDataRepository parent;

    public DecoratorRouteDataRepository(RouteDataRepository parent) {
        this.parent = parent;
    }

    @Override
    public Single<ParkingCurrOccupancy[]> getAllOccupancies() {
        return this.parent.getAllOccupancies();
    }

    @Override
    public Single<ParkingDailyStats[]> getAllParkingDailyStats() {
        return this.parent.getAllParkingDailyStats();
    }
    @Override
    public Single<Parking[]> getAllParking() {
        return this.parent.getAllParking();
    }

    @Override
    public Single<RouteETA> createRouteETA(@NonNull RouteRequest request) {
        return this.parent.createRouteETA(request);
    }

    @Override
    public Single<RouteETA> getETA(long id) {
        return this.parent.getETA(id);
    }

    @Override
    public Single<List<Route>> getAllRoutes() {
        return this.parent.getAllRoutes();
    }

    @Override
    public Single<Route> getRoute(long id) {
        return this.parent.getRoute(id);
    }
}
