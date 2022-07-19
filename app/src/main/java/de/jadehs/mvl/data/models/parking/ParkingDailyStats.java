package de.jadehs.mvl.data.models.parking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import de.jadehs.mvl.data.models.JsonSerializable;

public class ParkingDailyStats implements Parcelable, JsonSerializable {

    public static ParkingDailyStats[] allFromJson(JSONObject jsonObject) throws JSONException {
        JSONArray keys = jsonObject.names();
        if (keys == null) {
            return new ParkingDailyStats[0];
        }


        ParkingDailyStats[] parkingDailyStats = new ParkingDailyStats[keys.length()];

        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject parkingDailyStatData = jsonObject.getJSONObject(key);
            parkingDailyStats[i] = fromJson(key, parkingDailyStatData);
        }

        return parkingDailyStats;
    }

    public static ParkingDailyStats fromJson(String id, JSONObject jsonObject) throws JSONException {
        return new ParkingDailyStats(
                id,
                jsonObject.getInt("period"),
                jsonObject.getInt("spaces"),
                DayStat.allFromJson(jsonObject.getJSONArray("stats"))
        );
    }

    private final String id;
    private final int period;
    private final int spaces;
    private final DayStat[] stats;

    public ParkingDailyStats(String id, int period, int spaces, DayStat[] stats) {
        this.id = id;
        this.period = period;
        this.spaces = spaces;
        this.stats = stats;
    }

    private ParkingDailyStats(Parcel source) {
        id = source.readString();
        period = source.readInt();
        spaces = source.readInt();
        stats = source.createTypedArray(DayStat.CREATOR);
    }

    public String getId() {
        return id;
    }

    public int getPeriod() {
        return period;
    }

    public int getSpaces() {
        return spaces;
    }

    public DayStat[] getStats() {
        return stats;
    }

    /**
     * weekday of week.
     * <p>
     * use {@link Calendar} constants to pass a weekday
     *
     * @param weekday a constant from {@link Calendar}
     * @return the day stat of the requested day
     * @see Calendar#MONDAY
     * @see Calendar#TUESDAY
     * @see Calendar#WEDNESDAY
     * @see Calendar#THURSDAY
     * @see Calendar#FRIDAY
     * @see Calendar#SATURDAY
     * @see Calendar#SUNDAY
     */
    public DayStat getStatOfDay(int weekday) {
        return this.stats[weekday - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingDailyStats that = (ParkingDailyStats) o;
        return period == that.period && spaces == that.spaces && Objects.equals(id, that.id) && Arrays.equals(stats, that.stats);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, period, spaces);
        result = 31 * result + Arrays.hashCode(stats);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "ParkingDailyStats{" +
                "id='" + id + '\'' +
                ", period=" + period +
                ", spaces=" + spaces +
                ", stats=" + Arrays.toString(stats) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(period);
        dest.writeInt(spaces);
        dest.writeTypedArray(stats, flags);
    }

    @Override
    public Object toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("period", period);
        jsonObject.put("spaces", spaces);
        JSONArray statArray = new JSONArray();
        for (DayStat dayStat : stats) {
            statArray.put(dayStat.toJson());
        }
        jsonObject.put("stats", statArray);
        return jsonObject;
    }

    public static final Creator<ParkingDailyStats> CREATOR = new Creator<ParkingDailyStats>() {
        @Override
        public ParkingDailyStats createFromParcel(Parcel source) {
            return new ParkingDailyStats(source);
        }

        @Override
        public ParkingDailyStats[] newArray(int size) {
            return new ParkingDailyStats[size];
        }
    };


}
