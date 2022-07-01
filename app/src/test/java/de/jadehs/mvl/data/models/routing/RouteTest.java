package de.jadehs.mvl.data.models.routing;

import static org.junit.Assert.*;

import org.junit.Test;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;

import de.jadehs.mvl.data.models.Coordinate;

public class RouteTest {

    static class DistanceMockCoordinate extends Coordinate {

        public DistanceMockCoordinate(Coordinate coordinate) {
            super(coordinate.getLatitude(), coordinate.getLongitude());
        }

        public DistanceMockCoordinate(double latitude, double longitude) {
            super(latitude, longitude);
        }

        @Override
        public float distanceBetween(Coordinate coordinate) {
            return (float) this.subtract(coordinate).length();
        }

        @Override
        public Coordinate add(Coordinate coordinate) {
            return new DistanceMockCoordinate(super.add(coordinate));
        }

        @Override
        public Coordinate subtract(Coordinate coordinate) {
            return new DistanceMockCoordinate(super.subtract(coordinate));
        }

        @Override
        public Coordinate multiply(double factor) {
            return new DistanceMockCoordinate(super.multiply(factor));
        }
    }

    @Test
    public void getNextPoint() {
        List<Coordinate> coords = new ArrayList<>();

        coords.add(new DistanceMockCoordinate(10, 10));
        coords.add(new DistanceMockCoordinate(11, 10));
        coords.add(new DistanceMockCoordinate(11, 11));
        coords.add(new DistanceMockCoordinate(11, 12));
        Route r = new Route(100, "Testroute", coords);

        assertEquals(new DistanceMockCoordinate(11, 10), r.getNextPoint(new DistanceMockCoordinate(10.4, 10)));
    }
}