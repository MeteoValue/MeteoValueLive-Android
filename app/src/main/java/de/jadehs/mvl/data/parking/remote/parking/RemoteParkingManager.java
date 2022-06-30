package de.jadehs.mvl.data.parking.remote.parking;

import androidx.annotation.NonNull;

import org.json.JSONArray;

import de.jadehs.mvl.data.parking.models.parking.Parking;
import de.jadehs.mvl.data.parking.models.parking.ParkingCurrOccupancy;
import de.jadehs.mvl.data.parking.models.parking.ParkingDailyStats;
import de.jadehs.mvl.data.parking.remote.RemoteClient;
import de.jadehs.mvl.network.ParkingHeaderInterceptor;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class RemoteParkingManager extends RemoteClient implements ParkingManager {

    @NonNull
    public static final String HOST = "radar-flixbus.fokus.fraunhofer.de";

    @NonNull
    public static final String PARKING_BASE_URL = "parking/";


    public RemoteParkingManager(@NonNull OkHttpClient httpClient) {
        super(httpClient.newBuilder().addInterceptor(new ParkingHeaderInterceptor()).build());
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
     *
     * @param type which type of data to request
     * @return a new immutable HttpUrl instance
     */
    private static HttpUrl getUrlWithType(ParkingRequestType type) {
        // Builder could be a static variable
        return new HttpUrl.Builder()
                .scheme("https")
                .host(HOST)
                .addPathSegments(PARKING_BASE_URL)
                .addQueryParameter("type", type.getName()).build();
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
