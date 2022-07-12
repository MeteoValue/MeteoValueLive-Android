package de.jadehs.mvl.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import de.jadehs.mvl.data.remote.routing.Vehicle
import org.joda.time.Period
import org.joda.time.format.ISOPeriodFormat

class MainSharedPreferences(context: Context) {

    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    companion object SharedPreferencesConstants {
        const val KEY_VEHICLE_TYPE = "VEHICLE_TYPE"
        const val KEY_INTRO_DONE = "INTRO_DONE"
        const val KEY_MAX_TIME_DRIVING = "MAX_TIME_DRIVE"

    }

    var vehicleType: Vehicle
        get() {

            preferences.getString(KEY_VEHICLE_TYPE, Vehicle.TRUCK.id.toString())?.toInt()!!
                .let {
                    return Vehicle.fromInt(it)!!
                }

        }
        set(value) = preferences.edit().putString(KEY_VEHICLE_TYPE, value.id.toString()).apply()

    var introDone: Boolean
        get() = preferences.getBoolean(KEY_INTRO_DONE, false)
        set(value) = preferences.edit().putBoolean(KEY_INTRO_DONE, value).apply()

    var maxTimeDriving: Period
        get() = ISOPeriodFormat.standard()
            .parsePeriod(preferences.getString(KEY_MAX_TIME_DRIVING, "PT5H")!!)
        set(value) = preferences.edit().putString(KEY_MAX_TIME_DRIVING, value.toString()).apply()
}