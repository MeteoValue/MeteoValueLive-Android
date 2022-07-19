package de.jadehs.mvl.data.models.parking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import de.jadehs.mvl.data.models.JsonSerializable;

public class ParkingCurrOccupancy implements Parcelable, JsonSerializable {


    /**
     * returns all {@link ParkingCurrOccupancy} informations contained in the given JSONObject
     * <p>
     * the keys of the JSONObjects need to be the ids of the parking area
     *
     */
    public static ParkingCurrOccupancy[] allFromJson(JSONObject jsonObject) throws JSONException {
        JSONArray names = jsonObject.names();
        if (names == null)
            return new ParkingCurrOccupancy[0];
        ParkingCurrOccupancy[] occupancy = new ParkingCurrOccupancy[names.length()];
        for (int i = 0; i < names.length(); i++) {

            occupancy[i] = ParkingCurrOccupancy.fromJson(
                    names.getString(i),
                    jsonObject.getJSONObject(names.getString(i))
            );
        }
        return occupancy;
    }

    public static ParkingCurrOccupancy fromJson(String id, JSONObject jsonObject) throws JSONException {
        return new ParkingCurrOccupancy(id,
                jsonObject.getInt("occupied"),
                ISODateTimeFormat.dateTime().parseDateTime(jsonObject.getString("timestamp")));
    }

    private final String id;
    private final int occupied;
    private final DateTime timestamp;

    public ParkingCurrOccupancy(String id, int occupied, DateTime timestamp) {
        this.id = id;
        this.occupied = occupied;
        this.timestamp = timestamp;
    }

    private ParkingCurrOccupancy(Parcel source) {
        this.id = source.readString();
        this.occupied = source.readInt();
        this.timestamp = new DateTime(source.readLong());
    }


    public String getId() {
        return id;
    }

    public int getOccupied() {
        return occupied;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingCurrOccupancy that = (ParkingCurrOccupancy) o;
        return occupied == that.occupied && Objects.equals(id, that.id) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, occupied, timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "ParkingCurrOccupancy{" +
                "id='" + id + '\'' +
                ", occupied=" + occupied +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(occupied);
        dest.writeLong(timestamp.getMillis());
    }

    @Override
    public Object toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("occupied", this.occupied);
        object.put("timestamp", timestamp.toString());
        return object;
    }

    public static final Creator<ParkingCurrOccupancy> CREATOR = new Creator<ParkingCurrOccupancy>() {
        @Override
        public ParkingCurrOccupancy createFromParcel(Parcel source) {
            return new ParkingCurrOccupancy(source);
        }

        @Override
        public ParkingCurrOccupancy[] newArray(int size) {
            return new ParkingCurrOccupancy[size];
        }
    };
}
