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
            pointList.add(Coordinate.from3857(coord.getInt("x"), coord.getInt("y")));
        }
        return new Route(routeMetadata.getLong("id"), routeMetadata.getString("name"), pointList);
    }

    private final long id;
    @NonNull
    private final String name;
    @NonNull
    private final List<Coordinate> points;


    public Route(long id, @NonNull String name, @NonNull List<Coordinate> points) {
        this.id = id;
        this.name = name;
        this.points = Collections.unmodifiableList(points);
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

    public ListIterator<Coordinate> getIteratorAt(int index) {
        return this.points.listIterator(index);
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

            factor = factor < 0 ? 0 : factor > 1 ? 1 : factor;
            Coordinate pointOnLine = a.add(ab.multiply(factor));

            double currentDistance = pointOnLine.distanceBetween(p);


            if (currentDistance < shortDistance) {
                shortDistance = currentDistance;
                if (iterator.hasNext() && factor > 0) {
                    closestPoint = points.get(iterator.nextIndex());
                } else {
                    closestPoint = b;
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
