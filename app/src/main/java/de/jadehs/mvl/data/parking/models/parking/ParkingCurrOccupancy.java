package de.jadehs.mvl.data.parking.models.parking;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ParkingCurrOccupancy {

    /**
     * returns all {@link ParkingCurrOccupancy} informations contained in the given JSONObject
     * <p>
     * the keys of the JSONObjects need to be the ids of the parking area
     *
     * @param jsonObject
     * @return
     * @throws JSONException
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

    @Override
    public String toString() {
        return "ParkingCurrOccupancy{" +
                "id='" + id + '\'' +
                ", occupied=" + occupied +
                ", timestamp=" + timestamp +
                '}';
    }
}
