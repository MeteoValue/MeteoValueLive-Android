package de.jadehs.mvl.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import de.jadehs.mvl.BuildConfig;
import de.jadehs.mvl.data.parking.remote.parking.RemoteParkingManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ParkingHeaderInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String host = request.url().host();
        if (host.equalsIgnoreCase(RemoteParkingManager.HOST)) {
            Request newRequest = request.newBuilder().addHeader("Authorization", "Basic " + BuildConfig.PARKING_API_KEY).build();
            return chain.proceed(newRequest);
        }
        return chain.proceed(request);


    }
}

