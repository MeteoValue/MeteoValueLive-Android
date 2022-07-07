package de.jadehs.mvl.utils;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.routing.Route;

@RunWith(AndroidJUnit4.class)
public class DistanceHelperTest {


    private Route route;

    @Before
    public void setup() {
        List<Coordinate> points = new ArrayList<>();
        points.add(new Coordinate(53.536409, 8.133668));
        points.add(new Coordinate(53.534353078909476, 8.13653920603544));
        points.add(new Coordinate(53.53402592080422, 8.135953697919426));

        route = new Route(100, "Test", points, new ArrayList<>());
    }

    @Test
    public void getDistanceFromRoute() {


        assertEquals(212,
                DistanceHelper.getDistanceFromRoute(
                        route,
                        new Coordinate(53.53546051282803, 8.135030366323413)
                ),
                1);

    }

    @Test
    public void getDistanceFromToRoute() {

        assertEquals(310,
                DistanceHelper.getDistanceFromToRoute(route,
                        new Coordinate(53.53546051282803, 8.135030366323413),
                        new Coordinate(53.533502, 8.134757)),
                1);
    }
}