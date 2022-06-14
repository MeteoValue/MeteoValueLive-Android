package de.jadehs.mvl.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class MainSharedPreferences(context: Context) {

    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    companion object SharedPreferencesConstants {
        const val KEY_VEHICLE_TYPE = "VEHICLE_TYPE"
        const val KEY_INTRO_DONE = "INTRO_DONE"

        const val VEHICLE_TYPE_TRUCK = 0;
        const val VEHICLE_TYPE_BUS = 1;
    }

    var vehicleType: Int
        get() = preferences.getString(KEY_VEHICLE_TYPE, VEHICLE_TYPE_TRUCK.toString())!!.toInt()
        set(value) = preferences.edit().putString(KEY_VEHICLE_TYPE, value.toString()).apply()

    var introDone: Boolean
        get() = preferences.getBoolean(KEY_INTRO_DONE, false)
        set(value) = preferences.edit().putBoolean(KEY_INTRO_DONE, value).apply()

}