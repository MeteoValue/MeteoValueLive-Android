package de.jadehs.mvl.data.models.parking;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

public class ParkingDailyStats {

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
}
