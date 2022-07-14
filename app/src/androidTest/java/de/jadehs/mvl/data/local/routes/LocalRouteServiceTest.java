package de.jadehs.mvl.data.local.routes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.NoSuchElementException;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.routing.Route;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;

@RunWith(AndroidJUnit4.class)
public class LocalRouteServiceTest {


    private LocalRouteService routeService;

    @Before
    public void setup() {

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.routeService = new LocalRouteService(appContext);
    }


    @Test
    public void getAllRoutes() {
        @NonNull List<Route> routes = this.routeService.getAllRoutes().blockingGet();
        assertEquals(24, routes.size());
        Route route = null;
        for (Route r : routes) {
            if (r.getId() == 10000) {
                route = r;
                break;
            }
        }

        assertNotNull("debug route not found in route list", route);

        testTestRoute(route);
    }


    @Test
    public void testTestRoute() {
        @NonNull Route route = this.routeService.getRoute(10000).blockingGet();
        testTestRoute(route);


    }

    private void testTestRoute(Route route) {
        assertEquals(10000, route.getId());
        assertEquals("testroute", route.getName());


        List<Coordinate> points = route.getPoints();

        assertEquals(2, route.getPoints().size());

        Coordinate first = points.get(0);

        Coordinate last = points.get(1);

        assertEquals(47.307353, first.getLatitude(), 0.000001);
        assertEquals(10.8641353, first.getLongitude(), 0.000001);

        assertEquals(47.3073653, last.getLatitude(), 0.000001);
        assertEquals(10.8641442, last.getLongitude(), 0.000001);


        List<String> ids = route.getParkingIds();
        assertEquals(1, ids.size());

        assertEquals("DE-BY-000365", ids.get(0));
    }
}