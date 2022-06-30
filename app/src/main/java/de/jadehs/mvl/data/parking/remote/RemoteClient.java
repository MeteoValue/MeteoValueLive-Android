package de.jadehs.mvl.data.parking.remote;

import androidx.annotation.NonNull;

import java.io.IOException;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class RemoteClient {

    @NonNull
    private final OkHttpClient httpClient;


    public RemoteClient(@NonNull OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @NonNull
    protected OkHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Creates an Single instance which returnes the network requests response as string
     * <p>
     * its a get request
     * <p>
     * the Single is subscribedOn the android main thread, any subscriber should call observeOn on another thread (the network request is done on another thread)
     *
     * @param url the url to send the request to
     * @return new single instance
     */
    protected Single<String> getRequest(HttpUrl url) {
        return Single.<String>create(emitter -> {
            Request request = new Request.Builder().url(url).get().addHeader("Content-Type", "application/json").build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    emitter.onError(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    int code = response.code();
                    if (code < 200 || code >= 300) {
                        emitter.onError(new IllegalStateException("Unexpected response code"));
                        return;
                    }
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
}
