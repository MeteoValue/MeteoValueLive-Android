package de.jadehs.mvl.data;

import android.content.Context;

import de.jadehs.mvl.data.repositories.CachingRouteDataRepository;
import de.jadehs.mvl.data.repositories.MixedRouteDataRepository;
import okhttp3.OkHttpClient;

public interface RouteDataRepository extends ParkingService, RouteETAService, RouteService {


    class RouteDataBuilder {
        boolean withCaching;
        OkHttpClient client;

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

        public RouteDataRepository build(Context context) {
            OkHttpClient client = this.client;
            if (client == null) {
                client = new OkHttpClient();
            }
            RouteDataRepository repository = new MixedRouteDataRepository(client, context);

            if(withCaching){
                repository = new CachingRouteDataRepository(repository);
            }

            return repository;
        }
    }
}
