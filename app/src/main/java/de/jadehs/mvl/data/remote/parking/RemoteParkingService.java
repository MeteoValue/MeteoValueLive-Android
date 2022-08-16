package de.jadehs.mvl.data.remote.parking;

import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import de.jadehs.mvl.data.ParkingService;
import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;
import de.jadehs.mvl.data.models.parking.ParkingDailyStats;
import de.jadehs.mvl.data.models.parking.ParkingProperty;
import de.jadehs.mvl.data.remote.RemoteClient;
import de.jadehs.mvl.network.ParkingHeaderInterceptor;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class RemoteParkingService extends RemoteClient implements ParkingService {

    @NonNull
    private static final String PARKING_PROPERTIES_FILE = "parking_properties.json";

    @NonNull
    public static final String PARKING_BASE_URL = "parking/";

    @NonNull
    private final HttpUrl host;

    @NonNull
    private final AssetManager assets;


    public RemoteParkingService(@NonNull OkHttpClient httpClient, @NonNull HttpUrl host, @NonNull AssetManager assets) {
        super(httpClient.newBuilder().addInterceptor(new ParkingHeaderInterceptor()).build());
        this.host = host;
        this.assets = assets;
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

    @Override
    public Single<ParkingProperty[]> getParkingProperties(String id) {
        return getAllParkingProperties()
                .map(jsonObject -> jsonObject.getJSONArray(id))
                .map(ParkingProperty::allFromJson);
    }

    public Single<JSONObject> getAllParkingProperties() {
        return Single.fromSupplier(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(assets.open(PARKING_PROPERTIES_FILE)))) {
                        StringBuilder allLines = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            allLines.append(line).append("\n");
                        }
                        return allLines.toString();
                    }
                })
                .map(JSONObject::new);
    }

    /**
     * Constructs an {@link HttpUrl} instance which points to the parking url with the given request type
     *
     * @param type which type of data to request
     * @return a new immutable HttpUrl instance
     */
    private HttpUrl getUrlWithType(ParkingRequestType type) {
        // Builder could be a static variable
        return host.newBuilder()
                .addPathSegments(PARKING_BASE_URL)
                .addQueryParameter("type", type.getName())
                .build();
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
