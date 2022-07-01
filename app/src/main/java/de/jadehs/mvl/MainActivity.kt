package de.jadehs.mvl

import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import de.jadehs.mvl.data.models.Coordinate
import de.jadehs.mvl.data.ParkingManager
import de.jadehs.mvl.data.remote.parking.RemoteParkingManager
import de.jadehs.mvl.data.remote.routing.RemoteRouteETAManager
import de.jadehs.mvl.data.RouteETAManager
import de.jadehs.mvl.data.local.routes.LocalRouteManager
import de.jadehs.mvl.data.remote.routing.RouteRequest
import de.jadehs.mvl.data.remote.routing.Vehicle
import io.reactivex.rxjava3.kotlin.subscribeBy
import okhttp3.OkHttpClient
import org.joda.time.DateTime
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private lateinit var routeManager: LocalRouteManager
    private lateinit var routeETAManager: RouteETAManager
    private lateinit var parkingManager: ParkingManager
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var httpClient: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        httpClient = OkHttpClient.Builder().build() // change configurations if needed
        parkingManager =
            RemoteParkingManager(
                httpClient
            )
        routeETAManager =
            RemoteRouteETAManager(
                httpClient
            )
        routeManager =
            LocalRouteManager(
                this
            )


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_tour_settings,
                R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { item ->
            val handled = NavigationUI.onNavDestinationSelected(item, navController)
            if (handled) {
                drawerLayout.close()
            } else if (item.itemId == R.id.debug_menu) {


                routeManager.allRoutes.subscribeBy(
                    onSuccess = { routes ->
                        Log.d(TAG, "Routes loaded " + routes.toString())
                    },
                    onError = { exc ->
                        Log.e(TAG, "Error while loading routes", exc)

                    }
                )

                /*
                parkingManager.allParkingDailyStats.subscribeBy(
                    onSuccess = { data ->
                        Log.d(TAG, "Successfull response: " + Arrays.toString(data))
                    },
                    onError = { exception ->
                        Log.e(TAG, "exception while request form backend", exception)

                    })
                parkingManager.allOccupancies.subscribeBy(
                    onSuccess = { data ->
                        Log.d(TAG, "Successfull response: " + Arrays.toString(data))
                    },
                    onError = { exception ->
                        Log.e(TAG, "exception while request form backend", exception)
                    })

                parkingManager.allParking.subscribeBy(
                    onSuccess = { data ->
                        Log.d(TAG, "Successfull response: " + Arrays.toString(data))
                    },
                    onError = { exception ->
                        Log.e(TAG, "exception while request form backend", exception)
                    })

                var request = RouteRequest.newBuilder().apply {
                    from = Coordinate.fromSimpleString("49.454886,11.030073")
                    to = Coordinate.fromSimpleString("50.604461,10.644969")
                    this.starttime = DateTime.now()
                    this.vehicle = Vehicle.TRUCK
                }.build()
                routeETAManager.createRouteETA(request).subscribeBy(
                    onSuccess = { data ->
                        Log.d(TAG, "Successfull response: $data")
                    },
                    onError = { exception ->
                        Log.e(TAG, "exception while request form backend", exception)
                    }
                )*/
            }
            return@setNavigationItemSelectedListener handled
        }
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            TransitionManager.beginDelayedTransition(toolbar.parent as ViewGroup?, Fade())
            if (destination.id == R.id.nav_choose_vehicle) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}