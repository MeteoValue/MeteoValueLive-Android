package de.jadehs.mvl.data.models.reporting;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.JsonSerializable;

public class LocationReport extends Coordinate implements JsonSerializable {

    public static LocationReport fromLocation(Location location) {
        return new LocationReport(location.getLatitude(), location.getLongitude(), location.getTime(), location.getAccuracy(), location.getSpeed());
    }

    private final long timestamp;
    private final float accuracy;
    private final float speed;

    public LocationReport(double latitude, double longitude, long timestamp, float accuracy, float speed) {
        super(latitude, longitude);
        this.timestamp = timestamp;
        this.accuracy = accuracy;
        this.speed = speed;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = super.toJson();
        jsonObject.put("timestamp", timestamp);
        return jsonObject;
    }
}
