package de.jadehs.mvl.utils

import android.os.Build
import android.widget.TimePicker
import org.joda.time.Period

fun TimePicker.getMinuteCompat(): Int {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        return this.minute
    } else @Suppress("DEPRECATION") {

        return this.currentMinute
    }
}

fun TimePicker.getHourCompat(): Int {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return this.hour
    } else @Suppress("DEPRECATION") {
        return this.currentHour
    }
}


fun TimePicker.setMinuteCompat(minute: Int) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        this.minute = minute
    } else @Suppress("DEPRECATION") {

        this.currentMinute = minute
    }
}

fun TimePicker.setHourCompat(hour: Int) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.hour = hour
    } else @Suppress("DEPRECATION") {
        this.currentHour = hour
    }
}

fun TimePicker.getPeriod(): Period {
    return Period(getHourCompat(), getMinuteCompat(), 0, 0)
}

fun TimePicker.setPeriod(period: Period) {
    setHourCompat(period.hours)
    setMinuteCompat(period.minutes)
}