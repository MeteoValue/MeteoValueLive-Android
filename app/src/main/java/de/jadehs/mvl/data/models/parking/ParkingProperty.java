package de.jadehs.mvl.data.models.parking;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;

public enum ParkingProperty implements Parcelable {
    RESTROOM, RESTAURANT, GAS_STATION, HOTEL, SHOWER;
    private static final String TAG = "ParkingProperty";


    @NonNull
    public static ParkingProperty[] allFromJson(@NonNull JSONArray data) {
        ParkingProperty[] properties = new ParkingProperty[data.length()];
        for (int i = 0; i < data.length(); i++) {
            try {
                properties[i] = fromString(data.getString(i));
            } catch (JSONException | IllegalArgumentException exception) {
                Log.i(TAG, "allFromJson: exception while parsing ParkingProperty");
            }
        }
        return properties;
    }

    @NonNull
    public static ParkingProperty fromString(@NonNull String name) {
        return ParkingProperty.valueOf(name.toUpperCase(Locale.ROOT).replaceAll(" ", "_"));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name());
    }

    public static final Creator<ParkingProperty> CREATOR = new Creator<ParkingProperty>() {
        @Override
        public ParkingProperty createFromParcel(Parcel in) {
            return ParkingProperty.fromString(in.readString());
        }

        @Override
        public ParkingProperty[] newArray(int size) {
            return new ParkingProperty[size];
        }
    };
}
