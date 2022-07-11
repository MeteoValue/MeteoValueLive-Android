package de.jadehs.mvl.data.models.routing;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;

import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;

public class CurrentParkingETA {

    @NonNull
    private final Parking parking;
    private final int maxSpots;
    private final int destinationOccupiedSpots;
    private final double distance;
    private final RouteETA eta;
    @NonNull
    private final DateTime timestamp;
    private final ParkingCurrOccupancy currentOccupiedSpots;

    public CurrentParkingETA(@NonNull Parking parking, int maxSpots, int destinationOccupiedSpots, ParkingCurrOccupancy currentOccupiedSpots, double distance, RouteETA eta, @NonNull DateTime timestamp) {
        this.parking = parking;
        this.maxSpots = maxSpots;
        this.destinationOccupiedSpots = destinationOccupiedSpots;
        this.currentOccupiedSpots = currentOccupiedSpots;
        this.distance = distance;
        this.eta = eta;
        this.timestamp = timestamp;
    }

    @NonNull
    public Parking getParking() {
        return parking;
    }

    public int getMaxSpots() {
        return maxSpots;
    }

    public int getDestinationOccupiedSpots() {
        return destinationOccupiedSpots;
    }

    public double getDistance() {
        return distance;
    }

    public RouteETA getEta() {
        return eta;
    }

    @NonNull
    public DateTime getTimestamp() {
        return timestamp;
    }

    public ParkingCurrOccupancy getCurrentOccupiedSpots() {
        return currentOccupiedSpots;
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrentParkingETA{" +
                "parking=" + parking +
                ", maxSpots=" + maxSpots +
                ", destinationOccupiedSpots=" + destinationOccupiedSpots +
                ", distance=" + distance +
                ", eta=" + eta +
                ", timestamp=" + timestamp +
                ", currentOccupiedSpots=" + currentOccupiedSpots +
                '}';
    }
}
