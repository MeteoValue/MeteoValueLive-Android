package de.jadehs.mvl.data;

import android.content.Context;

import de.jadehs.mvl.data.repositories.CachingRouteDataRepository;
import de.jadehs.mvl.data.repositories.MixedRouteDataRepository;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public interface RouteDataRepository extends ParkingService, RouteETAService, RouteService {


    class RouteDataBuilder {
        boolean withCaching;
        OkHttpClient client;
        HttpUrl parkingHost = HttpUrl.parse("https://radar-flixbus.fokus.fraunhofer.de");
        HttpUrl etaHost = HttpUrl.parse("https://mvl-data.infoware.de");

        public boolean isWithCaching() {
            return withCaching;
        }

        public RouteDataBuilder setWithCaching(boolean withCaching) {
            this.withCaching = withCaching;
            return this;
        }

        public OkHttpClient getClient() {
            return client;
        }

        public RouteDataBuilder setClient(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public HttpUrl getParkingHost() {
            return parkingHost;
        }

        public RouteDataBuilder setParkingHost(HttpUrl parkingHost) {
            this.parkingHost = parkingHost;
            return this;
        }

        public HttpUrl getEtaHost() {
            return etaHost;
        }

        public RouteDataBuilder setEtaHost(HttpUrl etaHost) {
            this.etaHost = etaHost;
            return this;
        }

        public RouteDataRepository build(Context context) {
            OkHttpClient client = this.client;
            if (client == null) {
                client = new OkHttpClient();
            }
            RouteDataRepository repository = new MixedRouteDataRepository(client, context, this.parkingHost, this.etaHost);

            if (withCaching) {
                repository = new CachingRouteDataRepository(repository);
            }

            return repository;
        }
    }
}
