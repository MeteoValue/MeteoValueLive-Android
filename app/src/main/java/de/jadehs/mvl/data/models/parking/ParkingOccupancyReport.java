package de.jadehs.mvl.data.models.parking;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import de.jadehs.mvl.data.models.JsonSerializable;

public class ParkingOccupancyReport implements JsonSerializable {

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

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("parkingId", parkingId);
        object.put("timestamp", timestamp.toString());
        object.put("feedback", feedback.getName());
        return object;
    }


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
