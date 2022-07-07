package de.jadehs.mvl.data.models;

import static org.junit.Assert.*;

import org.junit.Test;

public class CoordinateTest {

    @Test
    public void from3857() {
        Coordinate coordinate = Coordinate.from3857(1209390, 5992387);

        assertEquals(10.8641353, coordinate.getLongitude(), 0.0000001);
        assertEquals(47.307353, coordinate.getLatitude(), 0.0000001);
    }

    @Test
    public void fromSimpleString() {
        String coordString = "10.8641353,47.307353";
        Coordinate coordinate = Coordinate.fromSimpleString(coordString);
        assertEquals(10.8641353, coordinate.getLatitude(), 0.0000001);
        assertEquals(47.307353, coordinate.getLongitude(), 0.0000001);
    }

    @Test
    public void add() {
        Coordinate coordinate = new Coordinate(10, 10);


        Coordinate positiveAdded = coordinate.add(new Coordinate(2, 2));
        assertEquals(12, positiveAdded.getLatitude(), 0);
        assertEquals(12, positiveAdded.getLongitude(), 0);


        Coordinate negativeAdded = coordinate.add(new Coordinate(-2, -2));
        assertEquals(8, negativeAdded.getLatitude(), 0);
        assertEquals(8, negativeAdded.getLongitude(), 0);

        Coordinate mixedAdded = coordinate.add(new Coordinate(-2, 2));
        assertEquals(8, mixedAdded.getLatitude(), 0);
        assertEquals(12, mixedAdded.getLongitude(), 0);

        Coordinate zeroAdded = coordinate.add(new Coordinate(0, 0));
        assertEquals(10, zeroAdded.getLatitude(), 0);
        assertEquals(10, zeroAdded.getLongitude(), 0);

        assertEquals(10, coordinate.getLatitude(), 0);
        assertEquals(10, coordinate.getLongitude(), 0);

    }

    @Test
    public void subtract() {

        Coordinate coordinate = new Coordinate(10, 10);
        Coordinate positiveSubtracted = coordinate.subtract(new Coordinate(2, 2));
        assertEquals(8, positiveSubtracted.getLatitude(), 0);
        assertEquals(8, positiveSubtracted.getLongitude(), 0);


        Coordinate negativeSubtracted = coordinate.subtract(new Coordinate(-2, -2));
        assertEquals(12, negativeSubtracted.getLatitude(), 0);
        assertEquals(12, negativeSubtracted.getLongitude(), 0);

        Coordinate mixedSubtracted = coordinate.subtract(new Coordinate(-2, 2));
        assertEquals(12, mixedSubtracted.getLatitude(), 0);
        assertEquals(8, mixedSubtracted.getLongitude(), 0);

        assertEquals(10, coordinate.getLatitude(), 0);
        assertEquals(10, coordinate.getLongitude(), 0);
    }

    @Test
    public void multiply() {
        Coordinate coordinate = new Coordinate(10, 10);
        Coordinate positive = coordinate.multiply(2);
        assertEquals(20, positive.getLatitude(), 0);
        assertEquals(20, positive.getLongitude(), 0);


        Coordinate negative = coordinate.multiply(-2);
        assertEquals(-20, negative.getLatitude(), 0);
        assertEquals(-20, negative.getLongitude(), 0);

        assertEquals(10, coordinate.getLatitude(), 0);
        assertEquals(10, coordinate.getLongitude(), 0);

    }

    @Test
    public void dot() {
        Coordinate coordinate = new Coordinate(10, 2);
        assertEquals(10 * 2 + 2 * 10,
                coordinate.dot(new Coordinate(2, 10)),
                0);
        assertEquals(10, coordinate.getLatitude(), 0);
        assertEquals(2, coordinate.getLongitude(), 0);
    }

    @Test
    public void distanceBetween() {
        Coordinate coordinate = new Coordinate(10, 10);

        assertEquals(1,
                coordinate.distanceBetween(new Coordinate(11, 10)),
                0);
        assertEquals(1,
                coordinate.distanceBetween(new Coordinate(10, 11)),
                0);


        assertEquals(Math.sqrt(2),
                coordinate.distanceBetween(new Coordinate(11, 11)),
                0);

    }

    @Test
    public void squaredLength() {

        assertEquals(200,
                new Coordinate(10, 10).squaredLength(),
                0);
        assertEquals(200,
                new Coordinate(-10, 10).squaredLength(),
                0);

        assertEquals(0,
                new Coordinate(0, 0).squaredLength(),
                0);
        assertEquals(1,
                new Coordinate(1, 0).squaredLength(),
                0);
        assertEquals(1,
                new Coordinate(0, 1).squaredLength(),
                0);
    }

    @Test
    public void length() {
        assertEquals(Math.sqrt(200),
                new Coordinate(10, 10).length(),
                0);
        assertEquals(Math.sqrt(200),
                new Coordinate(-10, 10).length(),
                0);

        assertEquals(0,
                new Coordinate(0, 0).length(),
                0);
        assertEquals(1,
                new Coordinate(1, 0).length(),
                0);
        assertEquals(1,
                new Coordinate(0, 1).length(),
                0);
    }

    @Test
    public void toSimpleString() {

        assertEquals("10.5,10.4", new Coordinate(10.5, 10.4).toSimpleString());

        assertEquals("10.56454813,10.4216564564", new Coordinate(10.56454813, 10.4216564564).toSimpleString());
        assertEquals("10.0,10.0", new Coordinate(10, 10).toSimpleString());
    }
}