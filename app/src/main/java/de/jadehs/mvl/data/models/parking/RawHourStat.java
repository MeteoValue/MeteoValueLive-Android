package de.jadehs.mvl.data.models.parking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class RawHourStat implements Parcelable {

    public static RawHourStat[] allFromJson(JSONArray jsonData) throws JSONException {
        RawHourStat[] hourStats = new RawHourStat[jsonData.length()];
        for (int i = 0; i < jsonData.length(); i++) {
            JSONObject dayStatData = jsonData.getJSONObject(i);
            hourStats[i] = fromJson(dayStatData);
        }
        return hourStats;
    }

    public static RawHourStat fromJson(JSONObject jsonObject) throws JSONException {
        return new RawHourStat(
                jsonObject.getInt("median"),
                jsonObject.getDouble("dev"),
                jsonObject.getInt("hour")
        );
    }

    private final int median;
    private final double dev;
    private final int hour;

    public RawHourStat(int median, double dev, int hour) {
        this.median = median;
        this.dev = dev;
        this.hour = hour;
    }

    private RawHourStat(Parcel source) {
        this.median = source.readInt();
        this.dev = source.readDouble();
        this.hour = source.readInt();
    }

    public int getMedian() {
        return median;
    }

    public double getDev() {
        return dev;
    }

    public int getHour() {
        return hour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawHourStat that = (RawHourStat) o;
        return median == that.median && Double.compare(that.dev, dev) == 0 && hour == that.hour;
    }

    @NonNull
    @Override
    public String toString() {
        return "RawHourStat{" +
                "median=" + median +
                ", dev=" + dev +
                ", hour=" + hour +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(median, dev, hour);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(median);
        dest.writeDouble(dev);
        dest.writeInt(hour);
    }

    public static final Creator<RawHourStat> CREATOR = new Creator<RawHourStat>() {
        @Override
        public RawHourStat createFromParcel(Parcel source) {
            return new RawHourStat(source);
        }

        @Override
        public RawHourStat[] newArray(int size) {
            return new RawHourStat[size];
        }
    };
}
