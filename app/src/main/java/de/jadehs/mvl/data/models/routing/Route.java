package de.jadehs.mvl.data.models.routing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import de.jadehs.mvl.data.models.Coordinate;

public class Route {


    public static Route fromJson(JSONObject object) throws JSONException {
        JSONObject routeMetadata = object.getJSONObject("route");
        JSONArray points = object.getJSONArray("points");
        List<Coordinate> pointList = new ArrayList<>();

        for (int i = 0; i < points.length(); i++) {
            JSONObject coord = points.getJSONObject(i);
            pointList.add(Coordinate.from3857(coord.getInt("lat"), coord.getInt("lng")));
        }

        List<String> parkingList = new ArrayList<>();
        if (object.has("parking")) {
            JSONArray parking = object.getJSONArray("parking");


            for (int i = 0; i < parking.length(); i++) {
                String id = points.getString(i);
                parkingList.add(id);
            }
        }
        return new Route(routeMetadata.getLong("id"), routeMetadata.getString("name"), pointList, parkingList);
    }

    private final long id;
    @NonNull
    private final String name;
    @NonNull
    private final List<Coordinate> points;
    @NonNull
    private final List<String> parkingIds;
    @NonNull
    private final Coordinate departure;
    @NonNull
    private final Coordinate destination;


    public Route(long id, @NonNull String name, @NonNull List<Coordinate> points, @NonNull List<String> parkingIds) {
        if (points.size() == 0) {
            throw new IllegalArgumentException("points list cannot be null");
        }

        this.id = id;
        this.name = name;
        this.points = Collections.unmodifiableList(points);
        this.parkingIds = Collections.unmodifiableList(parkingIds);
        int size = points.size();
        this.destination = points.get(size - 1);
        this.departure = points.get(0);
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public List<Coordinate> getPoints() {
        return points;
    }

    @NonNull
    public List<String> getParkingIds() {
        return parkingIds;
    }

    public ListIterator<Coordinate> getIteratorAt(int index) {
        return this.points.listIterator(index);
    }

    public ListIterator<Coordinate> getIteratorAt(Coordinate coordinate) {
        return this.points.listIterator(getIndexOfPoint(coordinate));
    }

    @NonNull
    public Coordinate getDeparture() {
        return departure;
    }

    @NonNull
    public Coordinate getDestination() {
        return destination;
    }

    public int getIndexOfPoint(Coordinate coordinate) {
        return this.points.indexOf(coordinate);
    }

    @Nullable
    public Coordinate getNextPoint(@NonNull Coordinate p) {
        if (this.points.size() < 1)
            return null;
        ListIterator<Coordinate> iterator = this.points.listIterator();
        Coordinate a = iterator.next();

        double shortDistance = p.distanceBetween(a);
        Coordinate closestPoint = a;
        while (iterator.hasNext()) {
            Coordinate b = iterator.next();
            Coordinate ab = b.subtract(a);
            Coordinate ap = p.subtract(a);


            double aDot = ab.dot(ap);


            double length = ab.squaredLength();
            double factor = aDot / length;

            double validFactor = factor < 0 ? 0 : factor > 1 ? 1 : factor;
            Coordinate pointOnLine = a.add(ab.multiply(validFactor));

            double currentDistance = pointOnLine.distanceBetween(p);


            // shorter distance or same distance and perpendicular to the line
            if (currentDistance < shortDistance || (currentDistance == shortDistance && factor >= 0 && factor <= 1)) {
                shortDistance = currentDistance;
                closestPoint = b;
                if (!iterator.hasNext()) {
                    if (factor > 1) {
                        closestPoint = null;
                    }
                }
            }

            a = b;
        }

        return closestPoint;
    }

    public float length() {
        if (this.points.size() < 2) {
            return 0;
        }
        float length = 0;
        Iterator<Coordinate> iterator = this.points.iterator();
        Coordinate last = iterator.next();
        while (iterator.hasNext()) {
            Coordinate next = iterator.next();
            length += last.distanceBetween(next);
            last = next;
        }
        return length;
    }

    @NonNull
    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", points=" + points +
                '}';
    }
}
