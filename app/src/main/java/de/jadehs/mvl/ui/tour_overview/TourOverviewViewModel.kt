package de.jadehs.mvl.ui.tour_overview

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.jadehs.mvl.MeteoApplication
import de.jadehs.mvl.data.LocationRouteETAFactory
import de.jadehs.mvl.data.RouteDataRepository
import de.jadehs.mvl.data.models.routing.CurrentRouteETA
import de.jadehs.mvl.data.models.routing.Route
import de.jadehs.mvl.data.remote.routing.Vehicle
import de.jadehs.mvl.services.RouteETAService
import de.jadehs.mvl.ui.PreferenceViewModel
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class TourOverviewViewModel(application: Application, val routeId: Long, vehicle: Vehicle?) :
    PreferenceViewModel(application) {

    private val dataRepository: RouteDataRepository =
        (application as MeteoApplication).getRepository()

    private val locationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val location =
                intent?.getParcelableExtra<Location>(RouteETAService.EXTRA_CURRENT_LOCATION)
            _currentLocation.postValue(location)

        }

    }
    private val routeETAReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val routeETA =
                intent?.getParcelableExtra<CurrentRouteETA>(RouteETAService.EXTRA_CURRENT_ROUTE_ETA)
            _currentRouteETA.postValue(routeETA)
            _currentRoute.postValue(routeETA?.route)

        }

    }
    private val localBroadcastManager = LocalBroadcastManager.getInstance(application).apply {
        registerReceiver(locationReceiver, IntentFilter(RouteETAService.ACTION_CURRENT_LOCATION))
        registerReceiver(routeETAReceiver, IntentFilter(RouteETAService.ACTION_CURRENT_ROUTE_ETA))
    }

    private val _currentRouteETA: MutableLiveData<CurrentRouteETA?> = MutableLiveData()

    val currentRouteETA: LiveData<CurrentRouteETA?>
        get() {
            return _currentRouteETA
        }

    private val _currentRoute: MutableLiveData<Route> = MutableLiveData()

    val currentRoute: LiveData<Route>
        get() {
            return _currentRoute
        }

    private val _currentLocation: MutableLiveData<Location> = MutableLiveData()

    val currentLocation: LiveData<Location>
        get() {
            return _currentLocation
        }


    fun startETAUpdates() {
        getApplication<Application>().startForegroundService(
            RouteETAService.newIntent(
                getApplication(),
                routeId
            )
        )
    }

    fun stopETAUpdates() {
        getApplication<Application>().stopService(
            Intent(getApplication(), RouteETAService::class.java)
        )
    }

    override fun onCleared() {
        super.onCleared()
        localBroadcastManager.unregisterReceiver(locationReceiver)
        localBroadcastManager.unregisterReceiver(routeETAReceiver)
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