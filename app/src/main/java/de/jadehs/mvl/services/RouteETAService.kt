package de.jadehs.mvl.services

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
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
        /**
         * Debug purposes
         */
        private const val TAG = "RouteETAService"

        /**
         * key of the route id extra entry needed to start this service
         *
         * Type needs to be a Long
         */
        const val EXTRA_ROUTE_ID = "de.jadehs.services.RouteETAService.route_id"

        /**
         * key of the optional vehicle id extra entry, to specify the vehicle the eta should get calculated
         *
         * Type needs to be a Int
         */
        const val EXTRA_VEHICLE_ID = "de.jadehs.services.RouteETAService.vehicle_id"

        /**
         * Action which is published when a new route eta is available
         *
         * Broadcast is published by a LocalBroadcast Manager
         */
        const val ACTION_CURRENT_ROUTE_ETA =
            "de.jadehs.services.RouteETAService.action_route_eta_data"

        /**
         * Key of the route eta extra entry which of the [ACTION_CURRENT_ROUTE_ETA] Broadcast
         *
         * The type is a Parcelable of the [CurrentRouteETA] class
         */
        const val EXTRA_CURRENT_ROUTE_ETA =
            "de.jadehs.services.RouteETAService.extra_route_eta_data"

        /**
         * Action which is published when a new Location is available
         *
         * Broadcast is published by a LocalBroadcast Manager
         */
        const val ACTION_CURRENT_LOCATION =
            "de.jadehs.services.RouteETAService.action_current_location"

        /**
         * Key of the Location extra entry which of the [ACTION_CURRENT_LOCATION] Broadcast
         *
         * The type is a Parcelable of the [Location] class
         */
        const val EXTRA_CURRENT_LOCATION =
            "de.jadehs.services.RouteETAService.extra_current_location"


        /**
         * interval of the eta updates
         *
         * This interval specifies the earliest eta update not the exact interval
         */
        private const val ETA_UPDATE_INTERVAL = 7 * 60 * 1000 // 7 Minutes

        /**
         * id of the foreground notification
         */
        private const val ONGOING_NOTIFICATION_ID: Int = 100

        /**
         * id of the channel the foreground notification is published in
         */
        private const val FOREGROUND_CHANNEL_ID = "eta_updates"

        /**
         * Creates an intent which can be used to start this service
         */
        @JvmStatic
        fun newIntent(context: Context, routeID: Long, vehicle: Vehicle? = null): Intent {
            return Intent(context, RouteETAService::class.java).apply {
                putExtras(newExtras(routeID, vehicle))
            }
        }

        /**
         * Creates a new Bundle which can be used to start this service
         */
        @JvmStatic
        fun newExtras(routeId: Long, vehicle: Vehicle? = null): Bundle {
            return Bundle().apply {
                putLong(EXTRA_ROUTE_ID, routeId)
                vehicle?.let {
                    putInt(EXTRA_VEHICLE_ID, it.id)
                }
            }
        }
    }


    /**
     * cancellationSource of the currentLocation request
     */
    private var currentLocationCancelationSource: CancellationTokenSource? = null
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var preferences: MainSharedPreferences
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var repository: RouteDataRepository

    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread

    private lateinit var locationProvider: FusedLocationProviderClient

    /**
     * Location request for the live location updates
     */
    private val locationRequest = LocationRequest.create().apply {
        interval = 2 * 1000
        fastestInterval = 500
        maxWaitTime = 5 * 1000
        priority = Priority.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 50f
    }

    /**
     * The current pending intent which should be used for a notification
     */
    private lateinit var notificationPendingIntent: PendingIntent

    /**
     * Builder for the notification which elevates this service into the foreground state
     */
    private lateinit var notificationBuilder: NotificationCompat.Builder

    /**
     * [NavDeepLinkBuilder] instance which points towards the [TourOverviewFragment]
     *
     * before building a deep link the route id needs to be set
     */
    private lateinit var deepLinkBuilder: NavDeepLinkBuilder


    /**
     * Location Callback which calls [onNewLocation] when a new location is retrieved
     */
    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let {
                onNewLocation(it)
            }
        }
    }

    /**
     * the last retrieved location
     *
     * publishes the data inside the setter via the [broadcastManager]
     */
    private var location: Location? = null
        set(value) {
            field = value
            publishCurrentLocation()
        }

    /**
     * the currently cached routeETA
     *
     * publishes the data inside the setter via the [broadcastManager]
     */
    private var routeETA: CurrentRouteETA? = null
        set(value) {
            field = value
            publishCurrentRouteETA()
        }

    /**
     * the cached route, updates [notificationPendingIntent] to a depp link if not null
     * and updates the shown notification
     */
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

    /**
     * eta factory with of the current route to retrieve new routeETA instances
     */
    private var routeETAFactory: LocationRouteETAFactory? = null

    /**
     * whether location updates are currently running
     */
    private var hasLocationUpdates = false

    /**
     * Contains all currently ongoing disposables
     *
     * If null then no Disposable is ongoing
     */
    private var isLoading: CompositeDisposable? = null

    /**
     * whether this service is currently in foreground
     */
    private var isForeground = false

    /**
     * Timestamp in milliseconds of the point in time where the lastETA update was retrieved
     *
     * timestamp is saved after the data was retrieved
     */
    private var lastETAUpdate: Long = 0


    override fun onCreate() {
        this.notificationBuilder = NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setContentTitle(this.getString(R.string.location_updates_notification_title))
            .setSmallIcon(R.drawable.ic_drive_eta)

        deepLinkBuilder = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.nav_tour_overview)
            .setComponentName(NavHostActivity::class.java)

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
            LocationServices.getFusedLocationProviderClient(this.applicationContext)

        this.handlerThread =
            HandlerThread("Location Receiver", Process.THREAD_PRIORITY_BACKGROUND)
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

        preferences.currentlyDriving = true
        loadCurrentLocation()
        loadRoute(newRoute)


        return START_NOT_STICKY
    }

    /**
     * Stops location updates, clears the route cache and clears any ongoing requests
     */
    override fun onDestroy() {
        stopLocationUpdates()
        clearOldRoute()
        preferences.recycle()
        handler.removeCallbacksAndMessages(null)
        handler.post(this::dismissLocationNotification)
        handlerThread.quitSafely()
    }

    /**
     * returns null because this service doesn't support binding
     */
    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    /**
     * deletes the currently cached route data and retrieves new route data with the given id
     * loads the a new routeETA
     *
     * @param id of the route
     * @param vehicle optional vehicle type which is used instead of the one specified by [MainSharedPreferences]
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

    /**
     * updates the current route eta if the location and route is already cached
     *
     * the current route eta is retrieved asynchronously
     */
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

    /**
     * updates the currently cached Location and if needed updates the currently RouteETA
     * in an async manner
     */
    private fun onNewLocation(loc: Location) {
        this.location = loc
        if (shouldUpdateETA()) {
            this.updateRouteETA()
        }
    }

    /**
     * checks if the currentETA should get updated
     * @return true if the currentETA should get updated
     */
    private fun shouldUpdateETA(): Boolean {
        return System.currentTimeMillis() - lastETAUpdate > ETA_UPDATE_INTERVAL
    }

    /**
     * if not currently in foreground a new notification is displayed to give this service a
     * foreground state
     */
    private fun startLocationForeground() {
        if (!isForeground) {
            this.startForeground(ONGOING_NOTIFICATION_ID, getForegroundNotification())
            isForeground = true
        }
    }

    /**
     * updates or creates the notification which is used to give this service a foreground state
     */
    private fun updateLocationNotification() {
        this.notificationManager.notify(ONGOING_NOTIFICATION_ID, getForegroundNotification())
    }

    private fun dismissLocationNotification() {
        this.notificationManager.cancel(ONGOING_NOTIFICATION_ID)
    }


    /**
     * Creates a new notification which represents the currently cached data
     */
    private fun getForegroundNotification(): Notification {
        return this.notificationBuilder.also {
            it.setContentIntent(this.notificationPendingIntent)
            route?.let { r ->
                it.setContentText(r.name)
            }
        }.build()
    }

    /**
     * adds the given [Disposable] to the [isLoading] [CompositeDisposable]
     *
     * A new  [CompositeDisposable] is created if no one exists or the current one is disposed
     *
     * @param disposable this [Disposable] is removed from the [isLoading] [CompositeDisposable]
     *
     * @see CompositeDisposable.delete
     */
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

    /**
     * removes the given [Disposable] from the [CompositeDisposable] without disposing it
     *
     * If after removal the [isLoading] [CompositeDisposable] stores no disposables anymore,
     * it is removed
     *
     * @param disposable this [Disposable] is removed from the [isLoading] [CompositeDisposable]
     *
     * @see CompositeDisposable.delete
     */
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

    /**
     * publishes the current cached data
     *
     * @see publishCurrentLocation
     * @see publishCurrentRouteETA
     */
    private fun publishCurrentData() {
        publishCurrentLocation()
        publishCurrentRouteETA()
    }

    /**
     * If a cached routeETA is available it is broadcast via the [RouteETAService.broadcastManager]
     */
    private fun publishCurrentRouteETA() {
        routeETA?.let {
            broadcastManager.sendBroadcast(getRouteETAIntent(it))
        }
    }

    /**
     * If a cached location is available it is broadcast via the [RouteETAService.broadcastManager]
     */
    private fun publishCurrentLocation() {
        location?.let {
            broadcastManager.sendBroadcast(getLocationIntent(it))
        }
    }


    /**
     * Creates an Intent which can be used to broadcast the given location
     * @param location location which is put inside the returned intent as extra data
     * @return a new Intent instance with the given location as extra data
     * @see EXTRA_CURRENT_LOCATION
     * @see ACTION_CURRENT_LOCATION
     */
    private fun getLocationIntent(location: Location): Intent {
        val intent = Intent(ACTION_CURRENT_LOCATION)
        intent.putExtra(EXTRA_CURRENT_LOCATION, location)
        return intent
    }

    /**
     * Creates an Intent which can be used to broadcast the given RouteETA
     * @param routeETA eta which is put inside the returned intent as extra data
     * @return a new Intent instance with the given routeETA as extra data
     * @see ACTION_CURRENT_ROUTE_ETA
     * @see EXTRA_CURRENT_ROUTE_ETA
     */
    private fun getRouteETAIntent(routeETA: CurrentRouteETA): Intent {
        val intent = Intent(ACTION_CURRENT_ROUTE_ETA)
        intent.putExtra(EXTRA_CURRENT_ROUTE_ETA, routeETA)
        return intent
    }


    /**
     * Loads a current location from the locationProvider and calls onNewLocation with the result
     */
    private fun loadCurrentLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.currentLocationCancelationSource = CancellationTokenSource()
            this.locationProvider.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                this.currentLocationCancelationSource?.token
            ).addOnSuccessListener { loc ->
                onNewLocation(loc)
                this.currentLocationCancelationSource = null
            }.addOnCanceledListener {
                this.currentLocationCancelationSource = null
            }.addOnFailureListener { t ->
                this.currentLocationCancelationSource = null
                Log.e(TAG, "loadCurrentLocation: Error which retrieving current location", t)
            }
        } else {
            Log.d(
                TAG,
                "loadCurrentLocation: Didn't have permission to request location updates"
            )
            stopSelf()
        }
    }

    /**
     * starts Location updates, if not enough permission were granted the service is stopeed
     */
    private fun startLocationUpdates() {
        if (!hasLocationUpdates) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                this.locationProvider.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    handler.looper
                )
                hasLocationUpdates = true
            } else {
                Log.d(
                    TAG,
                    "loadCurrentLocation: Didn't have permission to request location updates"
                )
                stopSelf()
            }

        }

    }

    /**
     * If running, stops location updates and the current location request
     */
    private fun stopLocationUpdates() {
        this.currentLocationCancelationSource?.cancel()
        if (hasLocationUpdates) {
            this.locationProvider.removeLocationUpdates(locationCallback)
            hasLocationUpdates = false
        }
    }

    /**
     * deletes all cached data and interrupts every data which is currently loading
     *
     * Locaiton isn't cleared
     */
    private fun clearOldRoute() {
        isLoading?.dispose()
        isLoading = null
        lastETAUpdate = 0
        route = null
        routeETA = null
        routeETAFactory = null
    }


}