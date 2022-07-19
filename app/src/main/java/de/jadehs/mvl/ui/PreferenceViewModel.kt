package de.jadehs.mvl.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.jadehs.mvl.settings.MainSharedPreferences

abstract class PreferenceViewModel(application: Application) : AndroidViewModel(application) {


    val preferences: MainSharedPreferences = MainSharedPreferences(application)


    override fun onCleared() {
        super.onCleared()
        preferences.recycle()
    }
}