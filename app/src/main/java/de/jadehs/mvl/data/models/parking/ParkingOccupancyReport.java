package de.jadehs.mvl.data.models.parking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import de.jadehs.mvl.data.models.JsonSerializable;

public class ParkingOccupancyReport implements JsonSerializable, Parcelable {

    @NonNull
    private final String parkingId;
    @NonNull
    private final DateTime timestamp;
    @NonNull
    private final ParkingOccupancy feedback;

    public ParkingOccupancyReport(@NonNull String parkingId, @NonNull DateTime timestamp, @NonNull ParkingOccupancy feedback) {
        this.parkingId = parkingId;
        this.timestamp = timestamp;
        this.feedback = feedback;
    }


    protected ParkingOccupancyReport(Parcel in) {
        parkingId = in.readString();
        timestamp = new DateTime(in.readLong());
        feedback = ParkingOccupancy.valueOf(in.readString());
    }


    @NonNull
    public String getParkingId() {
        return parkingId;
    }

    @NonNull
    public DateTime getTimestamp() {
        return timestamp;
    }

    @NonNull
    public ParkingOccupancy getFeedback() {
        return feedback;
    }

    @NonNull
    @Override
    public String toString() {
        return "ParkingOccupancyReport{" +
                "parkingId='" + parkingId + '\'' +
                ", timestamp=" + timestamp +
                ", feedback=" + feedback +
                '}';
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("parkingId", parkingId);
        object.put("timestamp", timestamp.toString());
        object.put("feedback", feedback.getName());
        return object;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parkingId);
        dest.writeLong(timestamp.getMillis());
        dest.writeString(feedback.getName());
    }

    public static final Creator<ParkingOccupancyReport> CREATOR = new Creator<ParkingOccupancyReport>() {
        @Override
        public ParkingOccupancyReport createFromParcel(Parcel in) {
            return new ParkingOccupancyReport(in);
        }

        @Override
        public ParkingOccupancyReport[] newArray(int size) {
            return new ParkingOccupancyReport[size];
        }
    };


    public enum ParkingOccupancy {
        LOW("LOW"), MEDIUM("MEDIUM"), HIGH("HIGH"), HIGHER("HIGHER");

        private final String name;

        ParkingOccupancy(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
