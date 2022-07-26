package de.jadehs.mvl.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import de.jadehs.mvl.BuildConfig;
import de.jadehs.mvl.data.remote.parking.RemoteParkingService;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ParkingHeaderInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest = request.newBuilder().addHeader("Authorization", "Basic " + BuildConfig.PARKING_API_KEY).build();
        return chain.proceed(newRequest);


    }
}

