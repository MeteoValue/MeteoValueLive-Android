package de.jadehs.mvl.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.List;

import de.jadehs.mvl.data.ParkingService;
import de.jadehs.mvl.data.RouteDataRepository;
import de.jadehs.mvl.data.RouteETAService;
import de.jadehs.mvl.data.RouteService;
import de.jadehs.mvl.data.local.routes.LocalRouteService;
import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;
import de.jadehs.mvl.data.models.parking.ParkingDailyStats;
import de.jadehs.mvl.data.models.parking.ParkingProperty;
import de.jadehs.mvl.data.models.routing.Route;
import de.jadehs.mvl.data.models.routing.RouteETA;
import de.jadehs.mvl.data.remote.parking.RemoteParkingService;
import de.jadehs.mvl.data.remote.routing.RemoteRouteETAService;
import de.jadehs.mvl.data.remote.routing.RouteRequest;
import io.reactivex.rxjava3.core.Single;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Route repository which retrieves the data from different data sources (Web and Local asset files)
 */
public class MixedRouteDataRepository implements RouteDataRepository {

    private final ParkingService parkingService;
    private final RouteETAService routeETAService;
    private final RouteService routeService;

    public MixedRouteDataRepository(OkHttpClient client, Context context, HttpUrl parkingHost, HttpUrl etaHost) {
        this.parkingService = new RemoteParkingService(client, parkingHost, context.getAssets());
        this.routeETAService = new RemoteRouteETAService(client, etaHost);
        this.routeService = new LocalRouteService(context);
    }

    @Override
    public Single<ParkingCurrOccupancy[]> getAllOccupancies() {
        return this.parkingService.getAllOccupancies();
    }

    @Override
    public Single<ParkingDailyStats[]> getAllParkingDailyStats() {
        return this.parkingService.getAllParkingDailyStats();
    }

    @Override
    public Single<Parking[]> getAllParking() {
        return this.parkingService.getAllParking();
    }

    @Override
    public Single<ParkingProperty[]> getParkingProperties(String id) {
        return this.parkingService.getParkingProperties(id);
    }

    @Override
    public Single<JSONObject> getAllParkingProperties() {
        return this.parkingService.getAllParkingProperties();
    }

    @Override
    public Single<RouteETA> createRouteETA(@NonNull RouteRequest request) {
        return this.routeETAService.createRouteETA(request);
    }

    @Override
    public Single<RouteETA> getETA(long id) {
        return this.routeETAService.getETA(id);
    }

    @Override
    public Single<List<Route>> getAllRoutes() {
        return this.routeService.getAllRoutes();
    }

    @Override
    public Single<Route> getRoute(long id) {
        return this.routeService.getRoute(id);
    }
}
