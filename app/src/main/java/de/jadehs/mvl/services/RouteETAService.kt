package de.jadehs.mvl.services

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.gms.location.*
import de.jadehs.mvl.MeteoApplication
import de.jadehs.mvl.R
import de.jadehs.mvl.data.LocationRouteETAFactory
import de.jadehs.mvl.data.RouteDataRepository
import de.jadehs.mvl.data.models.routing.CurrentRouteETA
import de.jadehs.mvl.data.models.routing.Route
import de.jadehs.mvl.data.remote.routing.Vehicle
import de.jadehs.mvl.settings.MainSharedPreferences
import de.jadehs.mvl.ui.NavHostActivity
import de.jadehs.mvl.ui.tour_overview.TourOverviewFragment
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class RouteETAService : Service() {

    companion object {
        private const val TAG = "RouteETAService"
        const val EXTRA_ROUTE_ID = "de.jadehs.services.RouteETAService.route_id"

        const val ACTION_CURRENT_ROUTE_ETA_DATA =
            "de.jadehs.services.RouteETAService.action_route_eta_data"
        const val EXTRA_CURRENT_ROUTE_ETA_DATA =
            "de.jadehs.services.RouteETAService.extra_route_eta_data"
        const val ACTION_CURRENT_LOCATION =
            "de.jadehs.services.RouteETAService.action_current_location"
        const val EXTRA_CURRENT_LOCATION =
            "de.jadehs.services.RouteETAService.extra_current_location"
        const val ETA_UPDATE_INTERVAL = 7 * 60 * 1000 // 7 Minutes

        const val ONGOING_NOTIFICATION_ID: Int = 100

        const val FOREGROUND_CHANNEL_ID = "eta_updates"
    }


    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var preferences: MainSharedPreferences
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var repository: RouteDataRepository

    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread

    private lateinit var locationProvider: FusedLocationProviderClient

    /**
     * Location request which updates every 60
     */
    private val locationRequest = LocationRequest.create().apply {
        interval = 60 * 1000
        fastestInterval = 40 * 1000
        priority = Priority.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 50f
    }

    private lateinit var notificationPendingIntent: PendingIntent

    private val notificationBuilder = NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
        .setContentTitle(this.getString(R.string.location_updates_notification_title))

    private val deepLinkBuilder = NavDeepLinkBuilder(this)
        .setGraph(R.navigation.mobile_navigation)
        .setDestination(R.id.nav_tour_overview)
        .setComponentName(NavHostActivity::class.java)


    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let {
                onNewLocation(it)
            }
        }
    }

    private var location: Location? = null
        set(value) {
            field = value
            publishCurrentLocation()
        }
    private var routeETA: CurrentRouteETA? = null
        set(value) {
            field = value
            publishCurrentRouteETA()
        }
    private var route: Route? = null
        set(value) {
            field = value
            if (value != null) {
                this.notificationPendingIntent =
                    deepLinkBuilder.setArguments(TourOverviewFragment.newInstanceBundle(value.id))
                        .createPendingIntent()
            } else {
                this.notificationPendingIntent =
                    Intent(this, NavHostActivity::class.java).let { notificationIntent ->
                        PendingIntent.getActivity(
                            this, 0, notificationIntent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    }

            }
            updateLocationNotification()

        }
    private var routeETAFactory: LocationRouteETAFactory? = null

    private var hasLocationUpdates = false

    private var isLoading: CompositeDisposable? = null

    private var isForeground = false

    private var lastETAUpdate: Long = 0


    override fun onCreate() {

        this.notificationPendingIntent =
            Intent(this, NavHostActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
        this.notificationManager = NotificationManagerCompat.from(this)

        this.notificationManager.createNotificationChannel(
            NotificationChannelCompat.Builder(
                FOREGROUND_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
                .setName(this.getString(R.string.foreground_channel_name))
                .setDescription(this.getString(R.string.foreground_channel_description))
                .build()
        )
        this.locationProvider =
            LocationServices.getFusedLocationProviderClient(this.applicationContext);

        this.handlerThread = HandlerThread("Location Receiver", Process.THREAD_PRIORITY_BACKGROUND)
        this.handlerThread.start()
        this.handler = Handler(this.handlerThread.looper)


        this.repository = (applicationContext as MeteoApplication).getRepository()

        this.broadcastManager = LocalBroadcastManager.getInstance(applicationContext)

        this.preferences = MainSharedPreferences(applicationContext)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent == null)
            return START_NOT_STICKY
        startLocationForeground()
        startLocationUpdates()

        val newRoute = intent.getLongExtra(EXTRA_ROUTE_ID, -1)
        if (newRoute == -1L) {
            return START_NOT_STICKY
        }

        // check if same route, if yes only publish current data
        route?.let { r ->
            if (r.id == newRoute) {
                publishCurrentData()
                return START_NOT_STICKY
            }
        }

        loadRoute(newRoute)


        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopLocationUpdates()
        clearOldRoute()
        handler.removeCallbacksAndMessages(null)
    }

    // no binding allowed
    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    /**
     * function to load a completely new route
     */
    private fun loadRoute(id: Long, vehicle: Vehicle? = null) {
        clearOldRoute()
        val v = vehicle ?: preferences.vehicleType
        handleDisposable(this.repository.getRoute(id)).subscribe { route ->
            this.route = route
            this.routeETAFactory = LocationRouteETAFactory(this.repository, route, v)
            updateRouteETA()
        }
    }

    private fun updateRouteETA() {
        location?.let {
            routeETAFactory?.getCurrentETAFrom(it)?.let { routeETASingle ->
                handleDisposable(routeETASingle).subscribeBy(
                    onSuccess = { routeETA ->
                        this.routeETA = routeETA
                        this.lastETAUpdate = System.currentTimeMillis()
                    }
                )
            }
        }
    }

    private fun onNewLocation(loc: Location) {
        this.location = loc
        if (shouldUpdateETA()) {
            this.updateRouteETA()
        }
    }

    private fun shouldUpdateETA(): Boolean {
        return lastETAUpdate - System.currentTimeMillis() > ETA_UPDATE_INTERVAL
    }

    private fun startLocationForeground() {
        if (!isForeground) {
            this.startForeground(ONGOING_NOTIFICATION_ID, getForegroundNotification())
            isForeground = true
        }
    }

    private fun updateLocationNotification() {
        this.notificationManager.notify(ONGOING_NOTIFICATION_ID, getForegroundNotification())
    }


    private fun getForegroundNotification(): Notification {
        return this.notificationBuilder.setContentIntent(this.notificationPendingIntent).build()
    }

    private fun addDisposable(disposable: Disposable) {

        isLoading?.let { compositeDisposable ->
            if (compositeDisposable.isDisposed) {
                isLoading = null
            } else {
                compositeDisposable.add(disposable)
            }
        }

        if (isLoading == null) {
            isLoading = CompositeDisposable(disposable)
        }

    }

    private fun deleteDisposable(disposable: Disposable) {

        isLoading?.let { compositeDisposable ->
            if (compositeDisposable.isDisposed) {
                isLoading = null
            } else {
                compositeDisposable.delete(disposable)

                if (compositeDisposable.size() == 0)
                    isLoading = null
            }
        }

    }

    /**
     * adds onSubscribe, onSuccess and onError callbacks which add or delete the disposable inside this service
     */
    private fun <T : Any> handleDisposable(single: Single<T>): Single<T> {
        var d: Disposable? = null
        return single.doOnSubscribe { disposable ->
            d = disposable
            addDisposable(disposable)
        }.doOnSuccess { _ ->
            d?.let { deleteDisposable(it) }
        }.doOnError { t ->
            Log.e(TAG, "Error while loading data in single", t)
            d?.let { deleteDisposable(it) }
        }
    }

    private fun publishCurrentData() {
        publishCurrentLocation()
        publishCurrentRouteETA()
    }

    private fun publishCurrentRouteETA() {
        routeETA?.let {
            broadcastManager.sendBroadcast(getRouteETAIntent(it))
        }
    }

    private fun publishCurrentLocation() {
        location?.let {
            broadcastManager.sendBroadcast(getLocationIntent(it))
        }
    }


    private fun getLocationIntent(location: Location): Intent {
        val intent = Intent(ACTION_CURRENT_LOCATION)
        intent.putExtra(EXTRA_CURRENT_LOCATION, location)
        return intent
    }

    private fun getRouteETAIntent(routeETA: CurrentRouteETA): Intent {

        val intent = Intent(ACTION_CURRENT_ROUTE_ETA_DATA)
        intent.putExtra(EXTRA_CURRENT_ROUTE_ETA_DATA, routeETA)
        return intent
    }


    private fun startLocationUpdates() {
        if (!hasLocationUpdates) {

            this.locationProvider.requestLocationUpdates(
                locationRequest,
                locationCallback,
                handler.looper
            )
            hasLocationUpdates = true
        }

    }

    private fun stopLocationUpdates() {
        if (hasLocationUpdates) {
            this.locationProvider.removeLocationUpdates(locationCallback)
            hasLocationUpdates = false
        }

    }

    private fun clearOldRoute() {
        isLoading?.dispose()
        isLoading = null
        lastETAUpdate = 0
        route = null
        routeETA = null
        routeETAFactory = null
    }


}