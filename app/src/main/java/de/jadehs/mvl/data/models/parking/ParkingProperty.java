package de.jadehs.mvl.data.models.parking;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;

public enum ParkingProperty {
    RESTROOM, RESTAURANT, GAS_STATION;
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


}
