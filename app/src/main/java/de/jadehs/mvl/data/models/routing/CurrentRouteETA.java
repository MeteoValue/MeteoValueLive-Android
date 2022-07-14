package de.jadehs.mvl.data.models.routing;

import androidx.annotation.NonNull;

import java.util.List;

public class CurrentRouteETA {

    @NonNull
    private final List<CurrentParkingETA> parkingETAs;
    @NonNull
    private final RouteETA destinationETA;
    @NonNull
    private final Route route;

    public CurrentRouteETA(@NonNull Route route, @NonNull List<CurrentParkingETA> parkingETAs, @NonNull RouteETA destinationETA) {
        this.parkingETAs = parkingETAs;
        this.destinationETA = destinationETA;
        this.route = route;
    }


    @NonNull
    public List<CurrentParkingETA> getParkingETAs() {
        return parkingETAs;
    }

    @NonNull
    public RouteETA getDestinationETA() {
        return destinationETA;
    }

    @NonNull
    public Route getRoute() {
        return route;
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrentRouteETA{" +
                "parkingETAs=" + parkingETAs +
                ", destinationETA=" + destinationETA +
                '}';
    }
}
