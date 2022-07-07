package de.jadehs.mvl.data.remote.parking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.runner.RunWith;

import de.jadehs.mvl.data.remote.routing.RemoteRouteETAService;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class RemoteRouteETAServiceTest {


    private RemoteRouteETAService routeEtaService;

    @Before
    public void setup() {

        this.routeEtaService = new RemoteRouteETAService(new OkHttpClient.Builder().build());
        // MockWebServer mockServer = new MockWebServer();

        // TODO make the code baseUrl capable
    }
}