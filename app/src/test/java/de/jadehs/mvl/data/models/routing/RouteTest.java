package de.jadehs.mvl.data.models.routing;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.jadehs.mvl.data.models.Coordinate;

public class RouteTest {


    private Route route;

    @Before
    public void setup() {
        List<Coordinate> coords = new ArrayList<>();

        coords.add(new Coordinate(10, 10));
        coords.add(new Coordinate(11, 10));
        coords.add(new Coordinate(11, 11));
        coords.add(new Coordinate(11, 12));
        route = new Route(100, "Testroute", coords);
    }

    @Test
    public void getNextPoint() {
        assertEquals(new Coordinate(10, 10), route.getNextPoint(new Coordinate(9.4, 10)));
        assertEquals(new Coordinate(11, 10), route.getNextPoint(new Coordinate(10.4, 10)));
        assertEquals(new Coordinate(11, 10), route.getNextPoint(new Coordinate(10.8, 10)));
        assertEquals(new Coordinate(11, 11), route.getNextPoint(new Coordinate(11, 10)));
        assertEquals(new Coordinate(11, 12), route.getNextPoint(new Coordinate(11, 13)));
    }

    @Test
    public void length() {
        assertEquals(3, route.length(), 0.0000001);
    }


    @Test
    public void getName() {
        assertEquals("Testroute", route.getName());
    }

    @Test
    public void getId() {
        assertEquals(100, route.getId());
    }

    @Test
    public void getIndexOfPoint() {
        assertEquals(0, route.getIndexOfPoint(new Coordinate(10, 10)));
        assertEquals(2, route.getIndexOfPoint(new Coordinate(11, 11)));
        assertEquals(-1, route.getIndexOfPoint(new Coordinate(11, 13)));
    }

    @Test
    public void getIteratorAtIndex() {
        assertEquals(0, route.getIteratorAt(0).nextIndex());
        assertEquals(2, route.getIteratorAt(2).nextIndex());
        assertThrows(IndexOutOfBoundsException.class, () -> route.getIteratorAt(100).nextIndex());
    }

    @Test
    public void getIteratorAtCoords() {
        assertEquals(0, route.getIteratorAt(new Coordinate(10, 10)).nextIndex());
        assertEquals(2, route.getIteratorAt(new Coordinate(11, 11)).nextIndex());
        assertThrows(IndexOutOfBoundsException.class, () -> route.getIteratorAt(new Coordinate(11, 13)).nextIndex());
    }

    @Test
    public void getPoints() {
        assertThrows(UnsupportedOperationException.class, () -> route.getPoints().add(0, null));
        assertThrows(UnsupportedOperationException.class, () -> route.getPoints().add(null));
        assertThrows(UnsupportedOperationException.class, () -> route.getPoints().remove(null));
        assertThrows(UnsupportedOperationException.class, () -> route.getPoints().addAll(new ArrayList<>()));
        assertThrows(UnsupportedOperationException.class, () -> route.getPoints().set(1, null));

    }
}