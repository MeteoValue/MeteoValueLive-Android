package de.jadehs.mvl.data.models.routing;

import androidx.annotation.NonNull;

import java.util.List;

public class CurrentRouteETA {

    @NonNull
    private final List<CurrentParkingETA> parkingETAs;
    @NonNull
    private final RouteETA destinationETA;

    public CurrentRouteETA(@NonNull List<CurrentParkingETA> parkingETAs, @NonNull RouteETA destinationETA) {
        this.parkingETAs = parkingETAs;
        this.destinationETA = destinationETA;
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
    @Override
    public String toString() {
        return "CurrentRouteETA{" +
                "parkingETAs=" + parkingETAs +
                ", destinationETA=" + destinationETA +
                '}';
    }
}
