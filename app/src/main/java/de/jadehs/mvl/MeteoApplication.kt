package de.jadehs.mvl

import android.app.Application
import de.jadehs.mvl.data.RouteDataRepository
import de.jadehs.mvl.data.repositories.CachingRouteDataRepository
import de.jadehs.mvl.data.repositories.MixedRouteDataRepository
import okhttp3.OkHttpClient

class MeteoApplication : Application() {


    private lateinit var httpClient: OkHttpClient
    private lateinit var routeDataRepository: RouteDataRepository

    override fun onCreate() {
        super.onCreate()
        httpClient = OkHttpClient.Builder().build()
        routeDataRepository = RouteDataRepository.RouteDataBuilder()
            .setWithCaching(true)
            .setClient(httpClient)
            .build(this)
    }


    fun getHttpClient(): OkHttpClient {
        return this.httpClient
    }

    fun getRepository(): RouteDataRepository {
        return this.routeDataRepository
    }


}