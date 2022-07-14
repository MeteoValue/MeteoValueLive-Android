package de.jadehs.mvl.data.models;

import static org.junit.Assert.*;

import android.os.Parcel;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CoordinatesTests {


    @Test
    public void toParcelable() {
        Parcel p = Parcel.obtain();

        Coordinate coordinate = new Coordinate(10.54, 12.45);
        coordinate.writeToParcel(p, 0);

        p.setDataPosition(0);
        Coordinate newCoordinate = Coordinate.CREATOR.createFromParcel(p);

        assertEquals(coordinate.getLatitude(), newCoordinate.getLatitude(), 0);
        assertEquals(coordinate.getLongitude(), newCoordinate.getLongitude(), 0);

    }
}
