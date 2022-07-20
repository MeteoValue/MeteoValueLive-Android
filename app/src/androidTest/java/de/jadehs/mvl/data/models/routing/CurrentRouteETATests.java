package de.jadehs.mvl.data.models.routing;

import static org.junit.Assert.*;

import android.os.Parcel;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.Random;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;
import de.jadehs.mvl.data.remote.routing.Vehicle;

@RunWith(AndroidJUnit4.class)
public class CurrentRouteETATests {

    public static CurrentRouteETA makeDummyData(Random random) {
        return makeDummyData(random, random.nextLong());
    }

    public static CurrentRouteETA makeDummyData(Random random, long routeId) {

        Coordinate currentLocation = new Coordinate(random.nextDouble() * 10 + 5, random.nextDouble() * 10 + 5);
        LinkedList<Coordinate> points = new LinkedList<>();
        points.add(currentLocation.subtract(new Coordinate(random.nextDouble(), random.nextDouble())));
        points.add(currentLocation.add(new Coordinate(random.nextDouble(), random.nextDouble())));
        points.add(currentLocation.add(new Coordinate(random.nextDouble() * 2 + 1, random.nextDouble() * 2 + 1)));
        LinkedList<String> parkingIds = new LinkedList<>();
        Parking parking1 = new Parking("TestParking-" + random.nextInt(),
                "Testname",
                new String[]{"https://www.google.com", "https://www.youtube.com"},
                points.get(0).getLongitude() + 0.0,
                points.get(0).getLatitude() + 0.7);
        Parking parking2 = new Parking("TestParking-" + random.nextInt(),
                "Testname",
                new String[]{"https://www.google.com", "https://www.youtube.com"},
                points.get(1).getLongitude() + 0.4,
                points.get(1).getLatitude() + 0.7);


        parkingIds.add(parking1.getId());
        parkingIds.add(parking2.getId());


        Route route = new Route(routeId, "Testroute", points, parkingIds);

        LinkedList<CurrentParkingETA> parkingETAS = new LinkedList<>();

        int maxSpots1 = (int) (random.nextDouble() * 100 + 20);
        int maxSpots2 = (int) (random.nextDouble() * 100 + 20);
        parkingETAS.add(new CurrentParkingETA(parking1,
                        maxSpots1,
                        (int) (maxSpots1 * random.nextDouble()),
                        new ParkingCurrOccupancy(
                                parking1.getId(),
                                (int) (maxSpots1 * random.nextDouble()),
                                DateTime.now().minus(Minutes.minutes((int) (random.nextDouble() * 40)))
                        ),
                        new RouteETA(
                                true,
                                "Route created",
                                random.nextLong(),
                                DateTime.now().minus(Minutes.minutes((int) (random.nextDouble() * 5))),
                                DateTime.now().plus(Hours.hours((int) (random.nextDouble() * 2))),
                                DateTime.now().plus(Hours.hours((int) (random.nextDouble() * 2 + 2))),
                                currentLocation,
                                currentLocation.add(new Coordinate(random.nextDouble(), random.nextDouble())),
                                Vehicle.TRUCK,
                                null
                        ),
                        DateTime.now()
                )
        );

        parkingETAS.add(new CurrentParkingETA(parking2,
                        maxSpots2,
                        (int) (maxSpots2 * random.nextDouble()),
                        new ParkingCurrOccupancy(
                                parking2.getId(),
                                (int) (maxSpots2 * random.nextDouble()),
                                DateTime.now().minus(Minutes.minutes((int) (random.nextDouble() * 40)))
                        ),
                        new RouteETA(
                                true,
                                "Route created",
                                random.nextLong(),
                                DateTime.now().minus(Minutes.minutes((int) (random.nextDouble() * 5))),
                                DateTime.now().plus(Hours.hours((int) (random.nextDouble() * 2))),
                                DateTime.now().plus(Hours.hours((int) (random.nextDouble() * 2 + 2))),
                                currentLocation,
                                currentLocation.add(new Coordinate(random.nextDouble(), random.nextDouble())),
                                Vehicle.TRUCK,
                                null
                        ),
                        DateTime.now()
                )
        );


        return new CurrentRouteETA(route, parkingETAS, new RouteETA(true,
                "Testroute",
                random.nextLong(),
                DateTime.now().minus(Minutes.minutes((int) (random.nextDouble() * 10))),
                DateTime.now().plus(Hours.hours((int) (random.nextDouble() * 2))),
                DateTime.now().plus(Hours.hours((int) (random.nextDouble() * 2 + 2))),
                new Coordinate(10.2, 10),
                new Coordinate(12, 11),
                Vehicle.TRUCK,
                new ViaList(new LinkedList<>())
        ), DateTime.now(), currentLocation);
    }

    private CurrentRouteETA currrentRouteETA;

    @Before
    public void setup() {
        this.currrentRouteETA = makeDummyData(new Random(100000));
    }


    @Test
    public void parcelable() {
        Parcel parcel = Parcel.obtain();
        this.currrentRouteETA.writeToParcel(parcel, 0);


        parcel.setDataPosition(0);
        CurrentRouteETA repaceld = CurrentRouteETA.CREATOR.createFromParcel(parcel);

        assertEquals(this.currrentRouteETA, repaceld);
        parcel.recycle();
    }

}
