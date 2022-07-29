package de.jadehs.mvl.data.remote.routing;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import de.jadehs.mvl.data.RouteETAService;
import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.routing.RouteETA;
import de.jadehs.mvl.data.remote.RemoteClient;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class RemoteRouteETAService extends RemoteClient implements RouteETAService {

    @NonNull
    private final HttpUrl host;

    private static final String BASE_URL = "ETAMonitoringService";

    private static final String CREATE_ROUTE_BASE_URL = "route";

    private static final String ETA_BASE_URL = "eta";

    public RemoteRouteETAService(@NonNull OkHttpClient httpClient, @NonNull HttpUrl host) {
        super(httpClient.newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build());
        this.host = host;
    }


    @Override
    public Single<RouteETA> createRouteETA(@NonNull RouteRequest request) {
        return getRequest(getCreateRouteUrl(request))
                .observeOn(Schedulers.io())
                .map(JSONObject::new)
                .map(RouteETA::fromJSON);
    }

    @Override
    public Single<RouteETA> getETA(long id) {
        return getRequest(getETAUrl(id))
                .observeOn(Schedulers.io())
                .map(JSONObject::new)
                .map(RouteETA::fromJSON);
    }


    /**
     * Constructs an {@link HttpUrl} instance which points to create route url
     *
     * @param request instance which describes the route
     * @return a new immutable HttpUrl instance
     */
    private HttpUrl getCreateRouteUrl(RouteRequest request) {
        // Builder could be a static variable
        HttpUrl.Builder builder = host.newBuilder()
                .addPathSegment(BASE_URL)
                .addPathSegment(CREATE_ROUTE_BASE_URL)
                .addQueryParameter("starttime", Long.toString(request.getStarttime().getMillis()))
                .addEncodedQueryParameter("from", request.getFrom().toSimpleString())
                .addEncodedQueryParameter("to", request.getTo().toSimpleString())
                .addQueryParameter("vehicle", request.getVehicle().toString().toLowerCase(Locale.ROOT));

        List<Coordinate> via = request.getVia();
        if (via != null && via.size() > 0) {
            builder.addQueryParameter("via", via.toString());
        }

        return builder.build();
    }

    private HttpUrl getETAUrl(long id) {
        // Builder could be a static variable
        return host.newBuilder()
                .addPathSegment(BASE_URL)
                .addPathSegment(ETA_BASE_URL)
                .addPathSegment(Long.toString(id))
                .build();
    }


}
