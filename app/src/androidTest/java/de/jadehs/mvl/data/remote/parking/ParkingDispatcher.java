package de.jadehs.mvl.data.remote.parking;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.QueueDispatcher;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;

/**
 * Mock dispatcher which first checks if it can respond because a fitting url is requested.
 * Otherwise returns a queued request.
 */
public class ParkingDispatcher extends QueueDispatcher {

    @NonNull
    private final Resources resources;

    public ParkingDispatcher(@NonNull Resources resources) {
        this.resources = resources;
    }

    @NonNull
    @Override
    public MockResponse dispatch(@NonNull RecordedRequest recordedRequest) throws InterruptedException {
        MockResponse response = handleParking(recordedRequest);
        if (response == null) {
            return super.dispatch(recordedRequest);
        }

        return response;
    }

    @Nullable
    private MockResponse handleParking(@NonNull RecordedRequest recordedRequest) {
        HttpUrl url = recordedRequest.getRequestUrl();
        if (url == null) {
            return null;
        }
        if (!url.pathSegments().contains("parking")) {
            return null;
        }
        try {
            String type = recordedRequest.getRequestUrl().queryParameter("type");
            if (type == null) {
                return null;
            }
            switch (type.toLowerCase(Locale.ROOT)) {
                case "base":
                    return getBaseResponse();
                case "daily_stats":
                    return getDailyResponse();
                case "curr_occupancy":
                    return getOccupancyResponse();
                default:
                    return null;
            }
        } catch (IOException e) {
            return new MockResponse().setResponseCode(500);
        }
    }

    private MockResponse getBaseResponse() throws IOException {
        return new MockResponse().setBody(
                new Buffer().readFrom(
                        resources.openRawResource(de.jadehs.mvl.test.R.raw.base_response)
                )
        );
    }

    private MockResponse getDailyResponse() throws IOException {
        return new MockResponse().setBody(
                new Buffer().readFrom(
                        resources.openRawResource(de.jadehs.mvl.test.R.raw.daily_response)
                )
        );
    }

    private MockResponse getOccupancyResponse() throws IOException {
        return new MockResponse().setBody(
                new Buffer().readFrom(
                        resources.openRawResource(de.jadehs.mvl.test.R.raw.occupancy_response)
                )
        );
    }
}
