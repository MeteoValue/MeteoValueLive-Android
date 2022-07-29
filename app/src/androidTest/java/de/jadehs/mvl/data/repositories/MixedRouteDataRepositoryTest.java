package de.jadehs.mvl.data.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.location.Location;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.jadehs.mvl.data.LocationRouteETAFactory;
import de.jadehs.mvl.data.RouteDataRepository;
import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.routing.CurrentRouteETA;
import de.jadehs.mvl.data.models.routing.Route;
import de.jadehs.mvl.data.remote.routing.Vehicle;
import io.reactivex.rxjava3.annotations.NonNull;
import okhttp3.OkHttpClient;

@RunWith(AndroidJUnit4.class)
public class MixedRouteDataRepositoryTest {


    private RouteDataRepository repository;
    private Route route;
    private LocationRouteETAFactory locationRouteETAFactory;

    @Before
    public void setup() {

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.repository = new RouteDataRepository.RouteDataBuilder().setWithCaching(true).build(appContext);
        this.route = this.repository.getRoute(157913972).blockingGet();
        this.locationRouteETAFactory = new LocationRouteETAFactory(this.repository, this.route, Vehicle.TRUCK);

    }

    @Test
    public void getCurrentETAFrom() {
        Location location = new Location("TEST");

        location.setLongitude(11.2675967);
        location.setLatitude(49.1451127);

        location.setTime(System.currentTimeMillis());
        location.setAccuracy(1);


        @NonNull CurrentRouteETA eta = this.locationRouteETAFactory.getCurrentETAFrom(location).blockingGet();

        System.out.println(eta);

        assertEquals(2, 1 + 1);

        List<Coordinate> coords = this.route.getPoints();
        Coordinate c = coords.get(coords.size() - 10);


        @NonNull CurrentRouteETA etaPast = this.locationRouteETAFactory.getCurrentETAFrom(c).blockingGet();
        System.out.println(etaPast);


    }
}