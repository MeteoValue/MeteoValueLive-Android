package de.jadehs.mvl.data.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.TestOnly;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;


/**
 * Holds Coordinates in EPSG:4326 WGS 84 format
 */
public class Coordinate implements Parcelable {


    /**
     * array which is used to calculate the distance to another coordinate.
     * <p>
     * It's only a class attribute to save array instantiations
     */
    @NonNull
    private final float[] otherLength = new float[1];

    /**
     * converts the given EPSG: 3857 coordinates to a Coordinate instance with EPSG:4326 format
     * <p>
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

    public static Coordinate fromLocation(Location location) {
        return new Coordinate(location.getLatitude(), location.getLongitude());
    }

    private final double longitude;
    private final double latitude;

    public Coordinate(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    private Coordinate(Parcel source) {
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    /**
     * adds the given coordinate from this coordinate and returns a new coordinate instance with the result
     *
     * @param coordinate the coordinate to add
     * @return a new coordinate instance
     */
    public Coordinate add(Coordinate coordinate) {
        return new Coordinate(this.latitude + coordinate.latitude,
                this.longitude + coordinate.longitude);
    }

    /**
     * subtracts the given coordinate from this coordinate and returns a new coordinate instance with the result
     *
     * @param coordinate the coordinate to subtract
     * @return a new coordinate instance
     */
    public Coordinate subtract(Coordinate coordinate) {
        return new Coordinate(this.latitude - coordinate.latitude,
                this.longitude - coordinate.longitude);
    }

    /**
     * multiplies the given factor from this coordinate and returns a new coordinate instance with the result
     *
     * @param factor the factor to multiply
     * @return a new coordinate instance
     */
    public Coordinate multiply(double factor) {
        return new Coordinate(this.latitude * factor, this.longitude * factor);
    }


    /**
     * Does calculate the dot product of the given coordinate and this one
     *
     * @param coordinate the given coordniate to calculate the dot product with
     * @return the dot product
     */
    public double dot(Coordinate coordinate) {
        return this.latitude * coordinate.latitude + this.longitude * coordinate.longitude;
    }

    /**
     * does calculate the distance from this coordinate to the given one in degrees
     *
     * @param coordinate the coordinate to calculate the distance to
     * @return the distance
     */
    public double distanceBetween(Coordinate coordinate) {
        return this.subtract(coordinate).length();
    }

    /**
     * squared length of this Coordinate (like the length of a vector) in degrees
     *
     * @return length in degrees
     */
    public double squaredLength() {
        return this.latitude * this.latitude + this.longitude * this.longitude;
    }

    /**
     * length of this Coordinate (like the length of a vector) in degrees
     *
     * @return length in degrees
     */
    public double length() {
        return Math.sqrt(squaredLength());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
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
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(1);
        formatter.setMaximumFractionDigits(Integer.MAX_VALUE);

        return formatter.format(this.latitude) +
                "," +
                formatter.format(this.longitude);
    }



    public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }

        @Override
        public Coordinate createFromParcel(Parcel source) {
            return new Coordinate(source);
        }
    };
}
