package de.jadehs.mvl.data.remote.parking;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Locale;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;

public class ParkingDispatcher extends Dispatcher {

    @NonNull
    private final Resources resources;

    public ParkingDispatcher(@NonNull Resources resources) {
        this.resources = resources;
    }

    @NonNull
    @Override
    public MockResponse dispatch(@NonNull RecordedRequest recordedRequest) {
        try {
            String type = recordedRequest.getRequestUrl().queryParameter("type");
            switch (type.toLowerCase(Locale.ROOT)) {
                case "base":
                    return getBaseResponse();
                case "daily_stats":
                    return getDailyResponse();
                case "curr_occupancy":
                    return getOccupancyResponse();
                default:
                    return new MockResponse().setResponseCode(500);
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
