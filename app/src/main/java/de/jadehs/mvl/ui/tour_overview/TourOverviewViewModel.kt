package de.jadehs.mvl.ui.tour_overview

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.jadehs.mvl.MeteoApplication
import de.jadehs.mvl.data.models.ReportArchive
import de.jadehs.mvl.data.models.parking.ParkingOccupancyReport
import de.jadehs.mvl.data.models.routing.CurrentRouteETA
import de.jadehs.mvl.data.models.routing.Route
import de.jadehs.mvl.data.remote.routing.Vehicle
import de.jadehs.mvl.services.RouteETAService
import de.jadehs.mvl.ui.PreferenceViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

/**
 * ViewModel for the [TourOverviewFragment]
 */
class TourOverviewViewModel(
    application: Application,
    val routeId: Long,
    private val vehicle: Vehicle?
) :
    PreferenceViewModel(application) {

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

    private val reportArchive: ReportArchive =
        getApplication<MeteoApplication>().getReportArchive(routeId)

    private val _currentRouteETA: MutableLiveData<CurrentRouteETA?> = MutableLiveData()

    /**
     * latest routeETA, null if no routeETA is currently available
     */
    val currentRouteETA: LiveData<CurrentRouteETA?>
        get() {
            return _currentRouteETA
        }

    private val _currentRoute: MutableLiveData<Route> = MutableLiveData()

    /**
     * latest route, null if no route is currently available
     */
    val currentRoute: LiveData<Route>
        get() {
            return _currentRoute
        }

    private val _currentLocation: MutableLiveData<Location> = MutableLiveData()

    /**
     * latest location, null if no location is currently available
     */
    val currentLocation: LiveData<Location>
        get() {
            return _currentLocation
        }


    /**
     * negate the driving status
     */
    fun triggerDrivingStatus() {
        val currentlyDriving = preferences.currentlyDriving
        preferences.currentlyDriving = !currentlyDriving
        if (currentlyDriving) {
            stopETAUpdates()
        } else {
            startETAUpdates()
        }
    }


    /**
     * starts eta updates
     */
    fun startETAUpdates() {
        getApplication<Application>().startForegroundService(
            RouteETAService.newIntent(
                getApplication(),
                routeId,
                vehicle
            )
        )
    }

    /**
     * stop eta updates
     */
    fun stopETAUpdates() {
        getApplication<Application>().stopService(
            Intent(getApplication(), RouteETAService::class.java)
        )
    }


    fun addParkingReport(parkingOccupancyReport: ParkingOccupancyReport) {
        reportArchive.addParkingReport(parkingOccupancyReport)
    }


    fun makeReportsZipFile(): Single<File> {
        return Single.fromCallable {
            return@fromCallable reportArchive.writePublishFile()
        }.subscribeOn(Schedulers.io())
    }

    override fun onCleared() {
        super.onCleared()
        localBroadcastManager.unregisterReceiver(locationReceiver)
        localBroadcastManager.unregisterReceiver(routeETAReceiver)
    }


    /**
     * Factory to create the TourOverviewViewModel instance
     * @param application application instance of the currently running application
     * @param routeId id of the route, the TourOverviewViewModel should calculate eta updates for
     * @param vehicle the vehicle type the routeETA updates should get calculated for.
     * If null the value current set by the [de.jadehs.mvl.settings.MainSharedPreferences.vehicleType]
     */
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