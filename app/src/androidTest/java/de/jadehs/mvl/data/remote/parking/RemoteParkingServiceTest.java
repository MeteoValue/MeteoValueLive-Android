package de.jadehs.mvl.data.remote.parking;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import de.jadehs.mvl.data.models.parking.Parking;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class RemoteParkingServiceTest {


    private RemoteParkingService parkingService;
    private MockWebServer mockServer;

    @Before
    public void setup() throws IOException {


        mockServer = new MockWebServer();

        mockServer.setDispatcher(new ParkingDispatcher(InstrumentationRegistry.getInstrumentation().getContext().getResources()));

        mockServer.start();
        this.parkingService = new RemoteParkingService(new OkHttpClient.Builder().build(), mockServer.url("/"));
    }

    @Test
    public void testBase() {
        Parking[] parkings = parkingService.getAllParking().blockingGet();

        assertEquals(2, parkings.length);
        Parking first = new Parking(
                "DE-BY-000363",
                "Nürnberg-Feucht Ost",
                new String[]{
                        "https://wcp.bayerninfo.de/by/jpg/pl_--a0009_-680_-4075_diber_vi-ne_feucht-o---cam1_0.jpg",
                        "https://wcp.bayerninfo.de/by/jpg/pl_--a0009_-680_-4075_diber_vi-ss_feucht-o---cam5_0.jpg"
                },
                11.20295,
                49.35705
        );
        Parking second = new Parking(
                "DE-BY-000364",
                "Nürnberg-Feucht West",
                new String[]{
                        "https://wcp.bayerninfo.de/by/jpg/pl_--a0009_-680_-4075_dinbg_vi-nn_feucht-w---cam5_0.jpg",
                        "https://wcp.bayerninfo.de/by/jpg/pl_--a0009_-680_-4075_dinbg_vi-ss_feucht-w---cam1_0.jpg"
                },
                11.20295,
                49.35705
        );

        assertEquals(first, parkings[0]);
        assertEquals(second, parkings[1]);
    }

    @After
    public void cleanup() throws IOException {
        this.mockServer.shutdown();
    }
}