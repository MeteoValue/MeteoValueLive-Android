package de.jadehs.mvl.data.models.routing;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;

import de.jadehs.mvl.data.models.parking.Parking;

public class CurrentParkingETA {

    @NonNull
    private final Parking parking;
    private final int maxSpots;
    private final int occupiedSpots;
    private final double distance;
    @NonNull
    private final RouteETA eta;
    @NonNull
    private final DateTime timestamp;

    public CurrentParkingETA(@NonNull Parking parking, int maxSpots, int occupiedSpots, double distance, @NonNull RouteETA eta, @NonNull DateTime timestamp) {
        this.parking = parking;
        this.maxSpots = maxSpots;
        this.occupiedSpots = occupiedSpots;
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

    public int getOccupiedSpots() {
        return occupiedSpots;
    }

    public double getDistance() {
        return distance;
    }

    @NonNull
    public RouteETA getEta() {
        return eta;
    }

    @NonNull
    public DateTime getTimestamp() {
        return timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrentParkingETA{" +
                "parking=" + parking +
                ", maxSpots=" + maxSpots +
                ", occupiedSpots=" + occupiedSpots +
                ", distance=" + distance +
                ", eta=" + eta +
                ", timestamp=" + timestamp +
                '}';
    }
}
