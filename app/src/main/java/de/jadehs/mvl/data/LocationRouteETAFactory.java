package de.jadehs.mvl.data;

import android.location.Location;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.RawHourStat;
import de.jadehs.mvl.data.models.routing.CurrentParkingETA;
import de.jadehs.mvl.data.models.routing.CurrentRouteETA;
import de.jadehs.mvl.data.models.routing.Route;
import de.jadehs.mvl.data.remote.routing.RouteRequest;
import de.jadehs.mvl.data.remote.routing.Vehicle;
import de.jadehs.mvl.utils.DistanceHelper;
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
    @NonNull
    public Single<CurrentRouteETA> getCurrentETAFrom(@NonNull final Location location) {
        return this.getCurrentETAFrom(Coordinate.fromLocation(location), new DateTime(location.getTime()));
    }

    /**
     * calculates the
     *
     * @param location location from where to start the eta calculations
     * @return single which resolves to a {@link CurrentRouteETA} instance,
     * which describes the route from the given location to the route destination
     * @see LocationRouteETAFactory#LocationRouteETAFactory(RouteDataRepository, Route, Vehicle)
     */
    @NonNull
    public Single<CurrentRouteETA> getCurrentETAFrom(@NonNull final Coordinate location) {
        return getCurrentETAFrom(location, DateTime.now());
    }

    /**
     * calculates the
     *
     * @param location location from where to start the eta calculations
     * @param at       point in time the eta calculation should assume the vehicle starts the route
     * @return single which resolves to a {@link CurrentRouteETA} instance,
     * which describes the route from the given location to the route destination
     * @see LocationRouteETAFactory#LocationRouteETAFactory(RouteDataRepository, Route, Vehicle)
     */
    @NonNull
    public Single<CurrentRouteETA> getCurrentETAFrom(@NonNull final Coordinate location, @NonNull DateTime at) {
        builder.setStarttime(at);

        builder.setFrom(location);

        final RouteRequest destinationRequest = builder.setTo(this.route.getDestination()).build();

        int locationIndex = this.route.getNextIndex(location);

        return this.repository.getParkings(this.route.getParkingIds())

                .flatMapSingle(parking -> {
                    if (locationIndex <= this.route.getNextIndex(parking.getCoordinate()))
                        return getCurrentParkingETA(builder, route, parking);
                    else
                        return getPastCurrentETA(builder, route, parking);
                })
                .toSortedList(this::compareParkingOnRoute)
                .flatMap(currentParkingETAS ->
                        this.repository.createRouteETA(destinationRequest)
                                .map(routeETA -> new CurrentRouteETA(route, currentParkingETAS, routeETA, at, location))
                );
    }

    /**
     * get an complete parking eta instance of a parking
     */
    private Single<CurrentParkingETA> getCurrentParkingETA(final RouteRequest.RouteRequestBuilder builder, final Route route, final Parking parking) {
        Coordinate onRoute = route.getClostestOnLine(parking.getCoordinate());
        builder.setTo(onRoute);
        return repository.createRouteETA(builder.build()).flatMap(parkingETA ->
                this.repository.getParkingDailyStat(parking.getId()).flatMap(parkingDailyStats -> {

                    int weekDay = parkingETA.getEtaWeather().getDayOfWeek();
                    int hourOfDay = parkingETA.getEtaWeather().getHourOfDay();


                    RawHourStat fittingStat = parkingDailyStats.getStatOfDay(weekDay).getStatOfHour(hourOfDay);

                    return this.repository.getOccupancy(parking.getId()).map(parkingCurrOccupancy ->
                            new CurrentParkingETA(
                                    parking,
                                    parkingDailyStats.getSpaces(),
                                    fittingStat.getMedian(),
                                    parkingCurrOccupancy,
                                    parkingETA,
                                    DateTime.now()
                            )
                    );
                })
        );
    }

    /**
     * get a parking eta instance of a parking which was already past
     */
    private Single<CurrentParkingETA> getPastCurrentETA(final RouteRequest.RouteRequestBuilder builder, final Route route, final Parking parking) {
        return this.repository.getOccupancy(parking.getId()).flatMap(parkingCurrOccupancy ->
                this.repository.getParkingDailyStat(parking.getId()).map(parkingDailyStats ->
                        new CurrentParkingETA(
                                parking,
                                parkingDailyStats.getSpaces(),
                                -1,
                                parkingCurrOccupancy,
                                null,
                                DateTime.now()
                        )
                )
        );
    }

    /**
     * compare the parking spots by the location on the route
     *
     * @param parking1 first parking to compare
     * @param parking2 second parking to compare
     * @return a number smaller than zero if the second argument is later on the route,
     * a number bigger than zero if the first argument is later on the route
     */
    private int compareParkingOnRoute(CurrentParkingETA parking1, CurrentParkingETA parking2) {
        Coordinate next1 = this.route.getNextPoint(parking1.getParking().getCoordinate());
        Coordinate next2 = this.route.getNextPoint(parking2.getParking().getCoordinate());
        if (next1 == null) {
            if (next2 == null)
                return 0;

            return 1;

        } else if (next2 == null) {
            return -1;
        }
        int index1 = this.route.getIndexOfPoint(next1);
        int index2 = this.route.getIndexOfPoint(next2);
        int comparedIndex = Integer.compare(index1, index2);
        if (comparedIndex == 0) {
            comparedIndex = Double.compare(
                    DistanceHelper.getDistanceFromToRoute(this.route, this.route.getDeparture(), next1),
                    DistanceHelper.getDistanceFromToRoute(this.route, this.route.getDeparture(), next2)
            );
        }
        return comparedIndex;
    }
}
