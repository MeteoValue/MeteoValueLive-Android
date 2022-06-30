package de.jadehs.mvl.data.parking.models;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Objects;

public class Coordinate {

    public static Coordinate fromSimpleString(String simpleString) {
        String[] parts = simpleString.split(",");
        return new Coordinate(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }

    private final double longitude;
    private final double latitude;

    public Coordinate(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.longitude, longitude) == 0 && Double.compare(that.latitude, latitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }

    @NonNull
    @Override
    public String toString() {
        return "Coordinate{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    /**
     * String contains lat and long separated by a comma
     *
     * @return a string describing this coordinate instance
     */
    public String toSimpleString() {
        return String.format(Locale.US, "%f,%f", this.latitude, this.longitude);
    }
}
