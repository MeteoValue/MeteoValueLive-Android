package de.jadehs.mvl.utils;

import android.location.Location;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.ListIterator;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.routing.Route;

public class DistanceHelper {


    public static double getDistanceFromRoute(@NonNull Route route, @NonNull Coordinate from) {
        return getDistanceFromToRoute(route, from, route.getDestination());
    }

    /**
     * @param route
     * @param from
     * @param to
     * @return distance or -1 if the to location is before the from location on the route
     */
    public static double getDistanceFromToRoute(@NonNull Route route, @NonNull Coordinate from, @NonNull Coordinate to) {
        float[] distanceBuffer = new float[1];
        Coordinate last = from;
        Coordinate terminationTo = route.getNextPoint(to);
        ListIterator<Coordinate> routeIterator = route.getIteratorAt(route.getNextPoint(last));
        if (route.getIndexOfPoint(terminationTo) < routeIterator.nextIndex()) {
            return -1;
        }
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
