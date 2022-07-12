package de.jadehs.mvl.data.models.routing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.joda.time.DateTime;

import java.util.Objects;

import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;

public class CurrentParkingETA {

    @NonNull
    private final Parking parking;
    private final int maxSpots;
    private final int destinationOccupiedSpots;
    private final double distance;
    @Nullable
    private final RouteETA eta;
    @NonNull
    private final DateTime timestamp;
    private final ParkingCurrOccupancy currentOccupiedSpots;

    public CurrentParkingETA(@NonNull Parking parking, int maxSpots, int destinationOccupiedSpots, ParkingCurrOccupancy currentOccupiedSpots, double distance, @Nullable RouteETA eta, @NonNull DateTime timestamp) {
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

    @Nullable
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentParkingETA that = (CurrentParkingETA) o;
        return maxSpots == that.maxSpots && destinationOccupiedSpots == that.destinationOccupiedSpots && Double.compare(that.distance, distance) == 0 && parking.equals(that.parking) && Objects.equals(eta, that.eta) && timestamp.equals(that.timestamp) && Objects.equals(currentOccupiedSpots, that.currentOccupiedSpots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parking, maxSpots, destinationOccupiedSpots, distance, eta, timestamp, currentOccupiedSpots);
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
