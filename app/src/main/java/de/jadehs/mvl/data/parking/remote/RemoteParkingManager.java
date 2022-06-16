package de.jadehs.mvl.data.parking.remote;

import androidx.annotation.NonNull;

import org.json.JSONArray;

import java.io.IOException;

import de.jadehs.mvl.data.parking.models.Parking;
import de.jadehs.mvl.data.parking.models.ParkingCurrOccupancy;
import de.jadehs.mvl.data.parking.models.ParkingDailyStats;
import de.jadehs.mvl.network.ParkingHeaderInterceptor;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RemoteParkingManager implements ParkingManager {

    @NonNull
    public static final String HOST = "radar-flixbus.fokus.fraunhofer.de";

    @NonNull
    public static final String PARKING_BASE_URL = "parking/";

    @NonNull
    private final OkHttpClient httpClient;


    public RemoteParkingManager(@NonNull OkHttpClient httpClient) {
        this.httpClient = httpClient.newBuilder().addInterceptor(new ParkingHeaderInterceptor()).build();
    }

    @Override
    public Single<ParkingCurrOccupancy[]> getAllOccupancies() {
        return getRequest(getUrlWithType(ParkingRequestType.CURR_OCCUPANCY))
                .observeOn(Schedulers.io())
                .map(JSONArray::new)
                .map(jsonArray -> jsonArray.getJSONObject(0).getJSONObject("data"))
                .map(ParkingCurrOccupancy::allFromJson);
    }

    @Override
    public Single<ParkingDailyStats[]> getAllParkingDailyStats() {
        return getRequest(getUrlWithType(ParkingRequestType.DAILY_STATS))
                .observeOn(Schedulers.io())
                .map(JSONArray::new)
                .map(jsonArray -> jsonArray.getJSONObject(0).getJSONObject("data"))
                .map(ParkingDailyStats::allFromJson);
    }

    @Override
    public Single<Parking[]> getAllParking() {
        return getRequest(getUrlWithType(ParkingRequestType.BASE))
                .observeOn(Schedulers.io())
                .map(JSONArray::new)
                .map(jsonArray -> jsonArray.getJSONObject(0).getJSONObject("data"))
                .map(Parking::allFromJson);
    }

    /**
     * Constructs an {@link HttpUrl} instance which points to the parking url with the given request type
     * @param type which type of data to request
     * @return a new immutable HttpUrl instance
     */
    private static HttpUrl getUrlWithType(ParkingRequestType type) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(HOST)
                .addPathSegments(PARKING_BASE_URL)
                .addQueryParameter("type", type.getName()).build();
    }


    /**
     * Creates an Single instance which returnes the network requests response as string
     *
     * its a get request
     *
     * the Single is subscribedOn the android main thread, any subscriber should call observeOn on another thread (the network request is done on another thread)
     * @param url the url to send the request to
     * @return new single instance
     */
    private Single<String> getRequest(HttpUrl url) {
        return Single.<String>create(emitter -> {
            Request request = new Request.Builder().url(url).get().addHeader("Content-Type", "application/json").build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    emitter.onError(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try (ResponseBody body = response.body()) {
                        String b;
                        if (body == null) {
                            b = "";
                        } else {
                            b = body.string();
                        }

                        emitter.onSuccess(b);
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }
            });
        }).subscribeOn(Schedulers.trampoline());
    }


    enum ParkingRequestType {
        DAILY_STATS("DAILY_STATS"), BASE("BASE"), CURR_OCCUPANCY("CURR_OCCUPANCY");

        private final String name;

        ParkingRequestType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
