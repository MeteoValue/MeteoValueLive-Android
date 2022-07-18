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

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;
import de.jadehs.mvl.data.remote.routing.Vehicle;

@RunWith(AndroidJUnit4.class)
public class CurrentRouteETATests {


    private LinkedList<Coordinate> points;
    private LinkedList<String> parkingIds;
    private Parking parking1;
    private Parking parking2;
    private Route route;
    private LinkedList<CurrentParkingETA> parkingETAS;
    private CurrentRouteETA currrentRouteETa;

    @Before
    public void setup() {
        this.points = new LinkedList<>();
        points.add(new Coordinate(10, 10));
        points.add(new Coordinate(11, 10));
        points.add(new Coordinate(12, 11));
        this.parkingIds = new LinkedList<>();
        parkingIds.add("TestParking1");
        parkingIds.add("TestParking2");

        this.parking1 = new Parking("TestParking1", "Testname1", new String[]{"https://www.google.com", "https://www.youtube.com"}, 10, 10.5);
        this.parking2 = new Parking("TestParking2", "Testname2", new String[]{"https://www.google.com", "https://www.youtube.com"}, 11.5, 10.5);

        this.route = new Route(100, "Testroute", points, parkingIds);

        this.parkingETAS = new LinkedList<>();

        parkingETAS.add(new CurrentParkingETA(parking1,
                        10,
                        9,
                        new ParkingCurrOccupancy(
                                parking1.getId(),
                                7,
                                DateTime.now().minus(Minutes.minutes(20))
                        ),
                        new RouteETA(
                                true,
                                "Testroute2",
                                1023123,
                                DateTime.now().minus(Days.days(10)),
                                DateTime.now().plus(Hours.hours(10)),
                                DateTime.now().plus(Hours.hours(11)),
                                new Coordinate(10.5, 10),
                                new Coordinate(10.7, 10),
                                Vehicle.TRUCK,
                                null
                        ),
                        DateTime.now()
                )
        );

        parkingETAS.add(new CurrentParkingETA(parking2,
                        15,
                        -1,
                        null,
                        new RouteETA(
                                true,
                                "Testroute3",
                                1023123,
                                DateTime.now().minus(Days.days(10)),
                                DateTime.now().plus(Hours.hours(10)),
                                DateTime.now().plus(Hours.hours(11)),
                                new Coordinate(10.5, 10),
                                new Coordinate(10.7, 10),
                                Vehicle.TRUCK,
                                null
                        ),
                        DateTime.now()
                )
        );


        this.currrentRouteETa = new CurrentRouteETA(route, parkingETAS, new RouteETA(true,
                "Testroute",
                10023123,
                DateTime.now().minus(Days.days(10)),
                DateTime.now().plus(Hours.hours(10)),
                DateTime.now().plus(Hours.hours(11)),
                new Coordinate(10.2, 10),
                new Coordinate(12, 11),
                Vehicle.TRUCK,
                new ViaList(new LinkedList<>())
        ));


    }


    @Test
    public void parcelable() {
        Parcel parcel = Parcel.obtain();
        this.currrentRouteETa.writeToParcel(parcel, 0);



        parcel.setDataPosition(0);
        CurrentRouteETA repaceld = CurrentRouteETA.CREATOR.createFromParcel(parcel);

        assertEquals(this.currrentRouteETa, repaceld);
        parcel.recycle();
    }

}
