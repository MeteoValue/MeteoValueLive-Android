package de.jadehs.mvl.data.models.parking;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.JsonSerializable;

public class Parking implements Parcelable, JsonSerializable {

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
                jsonObject.getDouble("lng"),
                jsonObject.getDouble("lat")
        );
    }

    private final String id;
    private final String name;
    private final Coordinate coordinate;
    private final Uri[] webcams;

    public Parking(String id, String name, String[] webcams, double lng, double lat) {
        this.id = id;
        this.name = name;
        Uri[] uris = new Uri[webcams.length];

        for (int i = 0; i < webcams.length; i++) {
            uris[i] = Uri.parse(webcams[i]);
        }

        this.webcams = uris;
        coordinate = new Coordinate(lat, lng);
    }

    private Parking(Parcel source) {
        id = source.readString();
        name = source.readString();
        coordinate = source.readParcelable(Coordinate.class.getClassLoader());
        webcams = source.createTypedArray(Uri.CREATOR);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeParcelable(coordinate, flags);
        dest.writeTypedArray(webcams, flags);
    }

    @Override
    public Object toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray webcamsArray = new JSONArray();
        for (Uri webcam : webcams) {
            webcamsArray.put(webcam.toString());
        }
        jsonObject.put("webcams", webcamsArray);
        jsonObject.put("name", this.name);
        jsonObject.put("lng", this.coordinate.getLongitude());
        jsonObject.put("lat", this.coordinate.getLatitude());
        return jsonObject;
    }

    public static final Creator<Parking> CREATOR = new Creator<Parking>() {
        @Override
        public Parking createFromParcel(Parcel source) {
            return new Parking(source);
        }

        @Override
        public Parking[] newArray(int size) {
            return new Parking[size];
        }
    };


}
