package de.jadehs.mvl.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import de.jadehs.mvl.data.remote.routing.Vehicle
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.ISOPeriodFormat

class MainSharedPreferences(context: Context) {

    private val preferencesCallback: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                KEY_VEHICLE_TYPE -> {
                    _vehicleTypeLiveData.value = vehicleType
                }
                KEY_MAX_TIME_DRIVING -> {
                    _maxTimeDrivingLiveData.value = maxTimeDriving
                }
                KEY_CURRENTLY_DRIVING -> {
                    _currentlyDrivingLiveData.value = currentlyDriving
                }
                KEY_CURRENT_DRIVING_LIMIT -> {
                    _currentDrivingLimitLiveData.value = currentDrivingLimit
                }
                KEY_CURRENT_ROUTE -> {
                    _currentRouteLiveData.value = currentRoute
                }
                KEY_LAST_DRIVING_STOPPED -> {
                    _lastDrivingStoppedLiveData.value = lastDrivingStopped
                }
            }
        }

    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            registerOnSharedPreferenceChangeListener(preferencesCallback)
        }


    companion object SharedPreferencesConstants {
        const val KEY_VEHICLE_TYPE = "VEHICLE_TYPE"
        const val KEY_INTRO_DONE = "INTRO_DONE"
        const val KEY_MAX_TIME_DRIVING = "MAX_TIME_DRIVE"
        const val KEY_CURRENTLY_DRIVING = "CURRENTLY_DRIVING"
        const val KEY_CURRENT_DRIVING_LIMIT = "CURRENT_DRIVING_LIMIT"
        const val KEY_CURRENT_ROUTE = "LAST_ROUTE"
        const val KEY_LAST_DRIVING_STOPPED = "LAST_DRIVING_STOPPED"
    }

    private val _vehicleTypeLiveData = MutableLiveData(vehicleType)

    /**
     * Currently selected vehicle type which should be used by eta calculations
     */
    val vehicleTypeLiveData: LiveData<Vehicle>
        get() {
            return _vehicleTypeLiveData
        }

    /**
     * Currently selected vehicle type which should be used by eta calculations
     */
    var vehicleType: Vehicle
        get() {
            preferences.getString(KEY_VEHICLE_TYPE, Vehicle.TRUCK.id.toString())?.toInt()!!
                .let {
                    return Vehicle.fromInt(it)!!
                }
        }
        set(value) = preferences.edit().putString(KEY_VEHICLE_TYPE, value.id.toString()).apply()

    /**
     * whether the intro was already show to the user
     */
    var introDone: Boolean
        get() = preferences.getBoolean(KEY_INTRO_DONE, false)
        set(value) = preferences.edit().putBoolean(KEY_INTRO_DONE, value).apply()

    private val _maxTimeDrivingLiveData = MutableLiveData(maxTimeDriving)

    /**
     * A time period the driver is allowed to drive without a breaks
     */
    val maxTimeDrivingLiveData: LiveData<Period>
        get() {
            return _maxTimeDrivingLiveData
        }

    /**
     * A time period the driver is allowed to drive without a breaks
     */
    var maxTimeDriving: Period
        get() = ISOPeriodFormat.standard()
            .parsePeriod(preferences.getString(KEY_MAX_TIME_DRIVING, "PT4H30M")!!)
        set(value) = preferences.edit().putString(KEY_MAX_TIME_DRIVING, value.toString()).apply()

    private val _currentRouteLiveData = MutableLiveData(currentRoute)

    /**
     * Whether the driver is currently driving
     */
    val currentRouteLiveData: LiveData<Long?>
        get() {
            return _currentRouteLiveData
        }

    /**
     * Last route id the user did start
     *
     * null if a route was never started
     */
    var currentRoute: Long?
        get() {
            if (preferences.contains(KEY_CURRENTLY_DRIVING))
                return preferences.getLong(KEY_CURRENT_ROUTE, -1).takeUnless { it == -1L }
            return null
        }
        set(value) {
            val editor = preferences.edit()
            value?.let {
                editor.putLong(KEY_CURRENT_ROUTE, value)
            } ?: kotlin.run {
                editor.remove(KEY_CURRENT_ROUTE)
            }
            editor.apply()
        }


    private val _currentlyDrivingLiveData = MutableLiveData(currentlyDriving)

    /**
     * Whether the driver is currently driving
     */
    val currentlyDrivingLiveData: LiveData<Boolean>
        get() {
            return _currentlyDrivingLiveData
        }

    /**
     * Whether the driver is currently driving
     *
     * null if the driver isn't currently driving
     */
    var currentlyDriving: Boolean
        get() {
            return preferences.getBoolean(KEY_CURRENTLY_DRIVING, false)
        }
        set(value) = preferences.edit().putBoolean(KEY_CURRENTLY_DRIVING, value).apply()


    private val _currentDrivingLimitLiveData = MutableLiveData(currentDrivingLimit)

    /**
     * the current time at which maxTimeDriving period is reached
     */
    val currentDrivingLimitLiveData: LiveData<DateTime?>
        get() {
            return _currentDrivingLimitLiveData
        }

    /**
     * the current time at which maxTimeDriving period is reached
     */
    var currentDrivingLimit: DateTime?
        get() {
            preferences.getLong(KEY_CURRENT_DRIVING_LIMIT, -1).let {
                if (it == -1L) {
                    return null
                }
                return DateTime(it)
            }
        }
        set(value) = preferences.edit().putLong(KEY_CURRENT_DRIVING_LIMIT, value?.millis ?: -1)
            .apply()


    private val _lastDrivingStoppedLiveData = MutableLiveData(lastDrivingStopped)

    /**
     * the current time at which maxTimeDriving period is reached
     */
    val lastDrivingStoppedLiveData: LiveData<DateTime?>
        get() {
            return _currentDrivingLimitLiveData
        }

    /**
     * the current time at which maxTimeDriving period is reached
     */
    var lastDrivingStopped: DateTime?
        get() {
            preferences.getLong(KEY_LAST_DRIVING_STOPPED, -1).let {
                if (it == -1L) {
                    return null
                }
                return DateTime(it)
            }
        }
        set(value) = preferences.edit().putLong(KEY_LAST_DRIVING_STOPPED, value?.millis ?: -1)
            .apply()


    /**
     * should be called when this preference isn't used anymore.
     *
     * Technically this instance is still usable after calling this function
     * it only causes the livedata to don't receive any data updates anymore
     */
    fun recycle() {
        this.preferences.unregisterOnSharedPreferenceChangeListener(preferencesCallback)
    }
}