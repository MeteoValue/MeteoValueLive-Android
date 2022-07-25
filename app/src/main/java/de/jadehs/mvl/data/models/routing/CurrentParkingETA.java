package de.jadehs.mvl.data.models.routing;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import de.jadehs.mvl.data.models.JsonSerializable;
import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;

public class CurrentParkingETA implements Parcelable, JsonSerializable {

    @NonNull
    private final Parking parking;
    private final int maxSpots;
    private final int destinationOccupiedSpots;
    @Nullable
    private final RouteETA eta;
    @NonNull
    private final DateTime timestamp;
    @NonNull
    private final ParkingCurrOccupancy currentOccupiedSpots;

    public CurrentParkingETA(@NonNull Parking parking, int maxSpots, int destinationOccupiedSpots, @NonNull ParkingCurrOccupancy currentOccupiedSpots, @Nullable RouteETA eta, @NonNull DateTime timestamp) {
        this.parking = parking;
        this.maxSpots = maxSpots;
        this.destinationOccupiedSpots = destinationOccupiedSpots;
        this.currentOccupiedSpots = currentOccupiedSpots;
        this.eta = eta;
        this.timestamp = timestamp;
    }

    private CurrentParkingETA(Parcel source) {
        parking = source.readParcelable(Parking.class.getClassLoader());
        maxSpots = source.readInt();
        destinationOccupiedSpots = source.readInt();
        eta = source.readParcelable(RouteETA.class.getClassLoader());
        timestamp = new DateTime(source.readLong());
        currentOccupiedSpots = source.readParcelable(ParkingCurrOccupancy.class.getClassLoader());
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

    @Nullable
    public RouteETA getEta() {
        return eta;
    }

    @NonNull
    public DateTime getTimestamp() {
        return timestamp;
    }

    @NonNull
    public ParkingCurrOccupancy getCurrentOccupiedSpots() {
        return currentOccupiedSpots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentParkingETA that = (CurrentParkingETA) o;
        return maxSpots == that.maxSpots && destinationOccupiedSpots == that.destinationOccupiedSpots && parking.equals(that.parking) && Objects.equals(eta, that.eta) && timestamp.equals(that.timestamp) && Objects.equals(currentOccupiedSpots, that.currentOccupiedSpots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parking, maxSpots, destinationOccupiedSpots, eta, timestamp, currentOccupiedSpots);
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrentParkingETA{" +
                "parking=" + parking +
                ", maxSpots=" + maxSpots +
                ", destinationOccupiedSpots=" + destinationOccupiedSpots +
                ", eta=" + eta +
                ", timestamp=" + timestamp +
                ", currentOccupiedSpots=" + currentOccupiedSpots +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(parking, flags);
        dest.writeInt(maxSpots);
        dest.writeInt(destinationOccupiedSpots);
        dest.writeParcelable(eta, flags);
        dest.writeLong(timestamp.getMillis());
        dest.writeParcelable(currentOccupiedSpots, flags);
    }

    @Override
    public Object toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("parking", parking.getId());
        object.put("maxSpots", maxSpots);
        object.put("destinationOccupiedSpots", destinationOccupiedSpots);
        if (eta != null)
            object.put("eta", eta.toJson());
        object.put("timestamp", timestamp.getMillis());
        object.put("currentOccupiedSpots", currentOccupiedSpots.toJson());
        return object;
    }

    public static final Creator<CurrentParkingETA> CREATOR = new Creator<CurrentParkingETA>() {
        @Override
        public CurrentParkingETA createFromParcel(Parcel source) {
            return new CurrentParkingETA(source);
        }

        @Override
        public CurrentParkingETA[] newArray(int size) {
            return new CurrentParkingETA[size];
        }
    };


}
