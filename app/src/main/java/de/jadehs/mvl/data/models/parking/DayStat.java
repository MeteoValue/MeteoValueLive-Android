package de.jadehs.mvl.data.models.parking;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class DayStat {

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
        RawHourStat[] rawHourStats = new RawHourStat[rawArray.length()];

        for (int i = 0; i < rawArray.length(); i++) {
            JSONObject rawHourStatData = rawArray.getJSONObject(i);
            RawHourStat rawHourStat = new RawHourStat(
                    rawHourStatData.getInt("median"),
                    rawHourStatData.getDouble("dev"),
                    rawHourStatData.getInt("hour")
            );
            rawHourStats[i] = rawHourStat;

        }
        return new DayStat(jsonObject.getString("img"), rawHourStats);
    }

    private final String image;
    private final RawHourStat[] stats;

    public DayStat(String image, RawHourStat[] stats) {
        this.image = image;
        this.stats = stats;
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

    public static class RawHourStat {
        private final int median;
        private final double dev;
        private final int hour;

        public RawHourStat(int median, double dev, int hour) {
            this.median = median;
            this.dev = dev;
            this.hour = hour;
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
    }
}
