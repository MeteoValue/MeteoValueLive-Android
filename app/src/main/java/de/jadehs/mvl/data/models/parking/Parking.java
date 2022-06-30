package de.jadehs.mvl.data.models.parking;

import android.net.Uri;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

import de.jadehs.mvl.data.models.Coordinate;

public class Parking {

    public static Parking[] allFromJson(JSONObject jsonObject) throws JSONException {
        JSONArray keys = jsonObject.names();
        if (keys == null) {
            return new Parking[0];
        }


        Parking[] parkingDailyStats = new Parking[keys.length()];

        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject parkingDailyStatData = jsonObject.getJSONObject(key);
            parkingDailyStats[i] = fromJson(key, parkingDailyStatData);
        }

        return parkingDailyStats;
    }

    public static Parking fromJson(String id, JSONObject jsonObject) throws JSONException {
        JSONArray webcamsData = jsonObject.getJSONArray("webcams");
        String[] webcams = new String[webcamsData.length()];
        for (int i = 0; i < webcamsData.length(); i++) {
            webcams[i] = webcamsData.getString(i);
        }

        return new Parking(
                id,
                jsonObject.getString("name"),
                webcams,
                jsonObject.getLong("lng"),
                jsonObject.getLong("lat")
        );
    }

    private final String id;
    private final String name;
    private final Coordinate coordinate;
    private final Uri[] webcams;

    public Parking(String id, String name, String[] webcams, long lng, long lat) {
        this.id = id;
        this.name = name;
        Uri[] uris = new Uri[webcams.length];

        for (int i = 0; i < webcams.length; i++) {
            uris[i] = Uri.parse(webcams[i]);
        }

        this.webcams = uris;
        coordinate = new Coordinate(lat, lng);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Uri[] getWebcams() {
        return webcams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parking parking = (Parking) o;
        return Objects.equals(id, parking.id) && Objects.equals(name, parking.name) && Objects.equals(coordinate, parking.coordinate) && Arrays.equals(webcams, parking.webcams);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, coordinate);
        result = 31 * result + Arrays.hashCode(webcams);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "Parking{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", coordinate=" + coordinate +
                ", webcams=" + Arrays.toString(webcams) +
                '}';
    }
}
