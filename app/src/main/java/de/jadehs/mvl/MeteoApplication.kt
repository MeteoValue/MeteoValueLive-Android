package de.jadehs.mvl

import android.app.Application
import de.jadehs.mvl.data.RouteDataRepository
import de.jadehs.mvl.data.models.ReportArchive
import de.jadehs.mvl.data.models.reporting.ETAParkingArchive
import de.jadehs.mvl.data.models.reporting.ParkingOccupancyReportArchive
import de.jadehs.mvl.data.models.reporting.RouteETAArchive
import de.jadehs.mvl.data.repositories.CachingRouteDataRepository
import de.jadehs.mvl.data.repositories.MixedRouteDataRepository
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

class MeteoApplication : Application() {


    private lateinit var httpClient: OkHttpClient
    private lateinit var routeDataRepository: RouteDataRepository
    private lateinit var parkingOccupancyReportArchive: ParkingOccupancyReportArchive
    private val routeETAArchives: MutableMap<Long, ReportArchive> = HashMap()

    override fun onCreate() {
        super.onCreate()
        httpClient = OkHttpClient.Builder().build()
        routeDataRepository = RouteDataRepository.RouteDataBuilder()
            .setWithCaching(true)
            .setClient(httpClient)
            .setParkingHost(HttpUrl.Builder().scheme("https").host("parking.g3sit.de").build())
            .setEtaHost(HttpUrl.Builder().scheme("https").host("eta.g3sit.de").build())
            .build(this)

        parkingOccupancyReportArchive = ParkingOccupancyReportArchive(filesDir)
    }


    fun getHttpClient(): OkHttpClient {
        return this.httpClient
    }

    fun getRepository(): RouteDataRepository {
        return this.routeDataRepository
    }

    fun getReportArchive(routeId: Long): ReportArchive {
        return this.routeETAArchives.computeIfAbsent(routeId) { key ->
            ReportArchive.fromContext(this, this.parkingOccupancyReportArchive, key)
        }
    }


}