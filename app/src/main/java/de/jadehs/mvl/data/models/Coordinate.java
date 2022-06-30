package de.jadehs.mvl.data.models;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Objects;


/**
 * Holds Coordinates in EPSG:4326 WGS 84 format
 */
public class Coordinate {

    /**
     * converts the given EPSG: 3857 coordinates to a Coordinate instance with EPSG:4326 format
     *
     * source: https://stackoverflow.com/a/40403522/8133841
     */
    public static Coordinate from3857(int x, int y) {
        double e = 2.7182818284;
        double X = 20037508.34;

        //converting the logitute from epsg 3857 to 4326
        double long4326 = (x * 180) / X;

        //converting the latitude from epsg 3857 to 4326 split in multiple lines for readability

        double lat4326 = y / (X / 180);
        double exponent = (Math.PI / 180) * lat4326;

        lat4326 = Math.atan(Math.pow(e, exponent));
        lat4326 = lat4326 / (Math.PI / 360);
        lat4326 = lat4326 - 90;

        return new Coordinate(lat4326, long4326);

    }

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
