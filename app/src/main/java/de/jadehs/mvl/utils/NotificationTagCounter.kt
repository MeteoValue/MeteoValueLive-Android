package de.jadehs.mvl.utils

import android.content.Context

object NotificationTagCounter {

    private val counterMap: MutableMap<String, Int> = HashMap()


    fun next(tag: String): Int {
        return counterMap.compute(tag) { _, oldValue ->
            (oldValue ?: 0) + 1
        }!!
    }


}