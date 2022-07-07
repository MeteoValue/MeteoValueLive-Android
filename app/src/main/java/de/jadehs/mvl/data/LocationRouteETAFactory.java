package de.jadehs.mvl.data;

import android.location.Location;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.parking.DayStat;
import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingDailyStats;
import de.jadehs.mvl.data.models.routing.CurrentParkingETA;
import de.jadehs.mvl.data.models.routing.CurrentRouteETA;
import de.jadehs.mvl.data.models.routing.Route;
import de.jadehs.mvl.data.remote.routing.RouteRequest;
import de.jadehs.mvl.data.remote.routing.Vehicle;
import de.jadehs.mvl.utils.DistanceHelper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class LocationRouteETAFactory {

    @NonNull
    private final RouteDataRepository repository;
    @NonNull
    private final Route route;
    @NonNull
    private final Vehicle vehicle;
    @NonNull
    private final RouteRequest.RouteRequestBuilder builder;


    public LocationRouteETAFactory(@NonNull final RouteDataRepository repository, @NonNull final Route route, @NonNull Vehicle vehicle) {
        this.repository = repository;
        this.route = route;
        this.vehicle = vehicle;
        this.builder = RouteRequest.newBuilder()
                .setVehicle(this.vehicle);
    }

    /**
     * calculates the
     *
     * @param location location from where to start the eta calculations
     * @return single which resolves to a {@link CurrentRouteETA} instance,
     * which describes the route from the given location to the route destination
     * @see LocationRouteETAFactory#LocationRouteETAFactory(RouteDataRepository, Route, Vehicle)
     */
    public Single<CurrentRouteETA> getCurrentETAFrom(final Location location) {
        return this.getCurrentETAFrom(Coordinate.fromLocation(location));
    }

    public Single<CurrentRouteETA> getCurrentETAFrom(final Coordinate location) {


        final DateTime currentTime = DateTime.now();
        builder.setStarttime(currentTime);

        builder.setFrom(location);

        final RouteRequest destinationRequest = builder.setTo(this.route.getDestination()).build();


        return this.repository.getAllParking()
                .flatMapObservable(Observable::fromArray)
                .filter(parking -> route.getParkingIds().contains(parking.getId()))
                .flatMapSingle(parking -> getCurrentParkingETA(builder, route, parking))
                // needs to be an lambda because of minSdk version
                .toSortedList((o1, o2) -> Double.compare(o1.getDistance(), o2.getDistance()))
                .flatMap(currentParkingETAS ->
                        this.repository.createRouteETA(destinationRequest)
                                .map(routeETA -> new CurrentRouteETA(currentParkingETAS, routeETA))
                );


    }

    private Single<CurrentParkingETA> getCurrentParkingETA(final RouteRequest.RouteRequestBuilder builder, final Route route, final Parking parking) {
        return repository.createRouteETA(builder.setTo(parking.getCoordinate()).build())
                .flatMap(routeETA ->
                        this.repository.getAllParkingDailyStats()
                                .map(parkingDailyStats -> {

                                    for (ParkingDailyStats parkingDailyStat : parkingDailyStats) {
                                        if (parkingDailyStat.getId().equals(parking.getId())) {
                                            return parkingDailyStat;
                                        }
                                    }
                                    throw new NoSuchElementException("couldn't find a daily stat of the given parking spot");

                                }).map(parkingDailyStats -> {

                                    int weekDay = routeETA.getEtaWeather().getDayOfWeek();
                                    int hourOfDay = routeETA.getEtaWeather().getHourOfDay();


                                    DayStat.RawHourStat fittingStat = parkingDailyStats.getStatOfDay(weekDay).getStatOfHour(hourOfDay);

                                    return new CurrentParkingETA(parking,
                                            parkingDailyStats.getSpaces(),
                                            fittingStat.getMedian(),
                                            DistanceHelper.getDistanceFromToRoute(route, routeETA.getFrom(), routeETA.getTo()),
                                            routeETA,
                                            builder.getStarttime()
                                    );
                                }));
    }
}
