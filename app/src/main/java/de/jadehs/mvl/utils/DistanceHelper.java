package de.jadehs.mvl.utils;

import android.location.Location;

import androidx.annotation.NonNull;

import java.util.Iterator;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.routing.Route;

public class DistanceHelper {


    public static double getDistanceFromRoute(@NonNull Route route, @NonNull Coordinate from) {
        return getDistanceFromToRoute(route, from, route.getDestination());
    }

    public static double getDistanceFromToRoute(@NonNull Route route, @NonNull Coordinate from, @NonNull Coordinate to) {
        float[] distanceBuffer = new float[1];
        Coordinate last = from;
        Coordinate terminationTo = route.getNextPoint(to);
        Iterator<Coordinate> routeIterator = route.getIteratorAt(route.getNextPoint(last));
        double distance = 0;
        while (routeIterator.hasNext()) {
            Coordinate next = routeIterator.next();
            if (next.equals(terminationTo)) {
                break;
            }
            Location.distanceBetween(
                    last.getLatitude(), last.getLongitude(),
                    next.getLatitude(), next.getLongitude(),
                    distanceBuffer);
            distance += distanceBuffer[0];
            last = next;
        }

        Location.distanceBetween(
                last.getLatitude(), last.getLongitude(),
                to.getLatitude(), to.getLongitude(),
                distanceBuffer
        );
        distance += distanceBuffer[0];
        return distance;
    }
}
