package de.jadehs.mvl.ui.tour_settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.jadehs.mvl.MeteoApplication
import de.jadehs.mvl.data.RouteDataRepository
import de.jadehs.mvl.data.models.routing.Route
import de.jadehs.mvl.ui.PreferenceViewModel
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class TourSettingsViewModel(application: Application) : PreferenceViewModel(application) {


    private val dataRepository: RouteDataRepository =
        (application as MeteoApplication).getRepository()

    private var isRequestingRoutes: Disposable? = null

    private val _allRoutes: MutableLiveData<List<Route>?> = MutableLiveData()

    val allRoutes: LiveData<List<Route>?>
        get() {
            if (_allRoutes.value.isNullOrEmpty() and (isRequestingRoutes?.isDisposed != false)) {
                isRequestingRoutes = dataRepository.allRoutes.subscribeBy(
                    onError = { t ->
                        Log.e("TourSettingsViewModel", "Error while retrieving route data", t)
                        isRequestingRoutes = null
                    },
                    onSuccess = { routes ->
                        _allRoutes.postValue(routes)
                        isRequestingRoutes = null
                    }
                )

            }
            return _allRoutes
        }

}