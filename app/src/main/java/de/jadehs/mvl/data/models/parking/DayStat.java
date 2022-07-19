package de.jadehs.mvl.data.models.parking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

import de.jadehs.mvl.data.models.JsonSerializable;

public class DayStat implements Parcelable, JsonSerializable {

    public static DayStat[] allFromJson(JSONArray jsonData) throws JSONException {
        DayStat[] dayStats = new DayStat[jsonData.length()];
        for (int i = 0; i < jsonData.length(); i++) {
            JSONObject dayStatData = jsonData.getJSONObject(i);
            dayStats[i] = fromJson(dayStatData);
        }
        return dayStats;
    }

    public static DayStat fromJson(JSONObject jsonObject) throws JSONException {
        JSONArray rawArray = jsonObject.getJSONArray("raw");
        RawHourStat[] rawHourStats = RawHourStat.allFromJson(rawArray);
        return new DayStat(jsonObject.getString("img"), rawHourStats);
    }

    private final String image;
    private final RawHourStat[] stats;

    public DayStat(String image, RawHourStat[] stats) {
        this.image = image;
        this.stats = stats;
    }

    private DayStat(Parcel source) {
        this.image = source.readString();
        this.stats = source.createTypedArray(RawHourStat.CREATOR);
    }


    public String getImage() {
        return image;
    }

    public RawHourStat[] getStats() {
        return stats;
    }

    /**
     * getter for RawHourStat functions
     *
     * @param hourOfDay hour of day starting at one
     * @return a RawHourStat describing the parking spot usage at the specified hour of day
     */
    public RawHourStat getStatOfHour(int hourOfDay) {
        if (hourOfDay < 1 || hourOfDay > 24)
            throw new IllegalArgumentException("hour of day needs to be between 1-24 (inclusive)");
        return this.stats[hourOfDay - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayStat dayStat = (DayStat) o;
        return Objects.equals(image, dayStat.image) && Arrays.equals(stats, dayStat.stats);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(image);
        result = 31 * result + Arrays.hashCode(stats);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "DayStat{" +
                "image='" + image + '\'' +
                ", stats=" + Arrays.toString(stats) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeTypedArray(stats, flags);
    }

    @Override
    public Object toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("image", image);
        JSONArray statArray = new JSONArray();
        for (RawHourStat stat : stats) {
            statArray.put(stat.toJson());
        }
        object.put("raw", statArray);
        return object;
    }

    public static final Creator<DayStat> CREATOR = new Creator<DayStat>() {
        @Override
        public DayStat createFromParcel(Parcel source) {
            return new DayStat(source);
        }

        @Override
        public DayStat[] newArray(int size) {
            return new DayStat[size];
        }
    };


}
