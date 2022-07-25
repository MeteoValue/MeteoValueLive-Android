package de.jadehs.mvl

import android.app.Application
import de.jadehs.mvl.data.RouteDataRepository
import de.jadehs.mvl.data.models.reporting.ParkingOccupancyReportArchive
import de.jadehs.mvl.data.models.reporting.RouteETAArchive
import de.jadehs.mvl.data.repositories.CachingRouteDataRepository
import de.jadehs.mvl.data.repositories.MixedRouteDataRepository
import okhttp3.OkHttpClient

class MeteoApplication : Application() {


    private lateinit var httpClient: OkHttpClient
    private lateinit var routeDataRepository: RouteDataRepository
    private lateinit var parkingOccupancyReportArchive: ParkingOccupancyReportArchive
    private val routeETAArchives: MutableMap<Long, RouteETAArchive> = HashMap()

    override fun onCreate() {
        super.onCreate()
        httpClient = OkHttpClient.Builder().build()
        routeDataRepository = RouteDataRepository.RouteDataBuilder()
            .setWithCaching(true)
            .setClient(httpClient)
            .build(this)

        parkingOccupancyReportArchive = ParkingOccupancyReportArchive(filesDir)
    }


    fun getHttpClient(): OkHttpClient {
        return this.httpClient
    }

    fun getRepository(): RouteDataRepository {
        return this.routeDataRepository
    }

    fun getParkingReportArchive(): ParkingOccupancyReportArchive {
        return this.parkingOccupancyReportArchive
    }

    fun getRouteETAArchive(routeId: Long): RouteETAArchive {
        return this.routeETAArchives.computeIfAbsent(routeId) { key ->
            RouteETAArchive(filesDir, key)
        }
    }


}