package de.jadehs.mvl.ui.tour_overview

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.jadehs.mvl.MeteoApplication
import de.jadehs.mvl.data.LocationRouteETAFactory
import de.jadehs.mvl.data.RouteDataRepository
import de.jadehs.mvl.data.models.routing.CurrentRouteETA
import de.jadehs.mvl.data.remote.routing.Vehicle
import de.jadehs.mvl.ui.PreferenceViewModel
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class TourOverviewViewModel(application: Application, routeId: Long, vehicle: Vehicle?) :
    PreferenceViewModel(application) {

    private val dataRepository: RouteDataRepository =
        (application as MeteoApplication).getRepository()

    private var routeETAFactory: LocationRouteETAFactory? = null

    private var loadWhenReady: Location? = null

    private var requestingDisposable: Disposable? = null

    private val _currentRouteETA: MutableLiveData<CurrentRouteETA?> = MutableLiveData()

    init {
        dataRepository.getRoute(routeId).subscribeBy(
            onSuccess = { route ->
                routeETAFactory =
                    LocationRouteETAFactory(
                        dataRepository,
                        route,
                        vehicle ?: preferences.vehicleType
                    )

                loadWhenReady?.let { loc ->
                    this.updateRouteETA(loc)
                    loadWhenReady = null
                }


            },
            onError = { t ->
                Log.e(
                    "TourOverviewViewModel", "Error while retrieving route in constructor" +
                            "Whole ViewModel is unusable because of this error", t
                )
            }
        )
    }

    val currentRouteETA: LiveData<CurrentRouteETA?>
        get() {
            return _currentRouteETA
        }

    fun updateRouteETA(location: Location) {
        if (requestingDisposable?.isDisposed == false)
            return

        if (this.routeETAFactory == null) {
            loadWhenReady = location
            return
        }


        this.requestingDisposable =
            this.routeETAFactory?.let {
                it.getCurrentETAFrom(location).subscribeBy(
                    onError = { throwable ->
                        Log.e(
                            "TourOverviewViewModel",
                            "Error while retrieving a new route eta",
                            throwable
                        )
                        requestingDisposable = null
                    },
                    onSuccess = { eta ->
                        this._currentRouteETA.postValue(eta)
                        requestingDisposable = null
                    }
                )
            }
    }

    override fun onCleared() {
        super.onCleared()
        if (this.requestingDisposable?.isDisposed == false)
            this.requestingDisposable?.dispose()
    }


    class TourOverviewViewModelFactory(
        private val application: Application,
        private val routeId: Long,
        private val vehicle: Vehicle?
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val constructor = modelClass.getConstructor(
                Application::class.java,
                Long::class.java,
                Vehicle::class.java
            )
            return constructor.newInstance(application, routeId, vehicle)
        }
    }
}