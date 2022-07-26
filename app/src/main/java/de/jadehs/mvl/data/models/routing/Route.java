package de.jadehs.mvl.data.models.routing;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import de.jadehs.mvl.data.models.Coordinate;

public class Route implements Parcelable {


    public static Route fromJson(JSONObject object) throws JSONException {
        JSONObject routeMetadata = object.getJSONObject("route");
        JSONArray points = object.getJSONArray("points");
        List<Coordinate> pointList = new ArrayList<>();

        for (int i = 0; i < points.length(); i++) {
            JSONObject coord = points.getJSONObject(i);
            pointList.add(Coordinate.from3857(coord.getInt("lng"), coord.getInt("lat")));
        }

        List<String> parkingList = new ArrayList<>();
        if (object.has("parking")) {
            JSONArray parking = object.getJSONArray("parking");


            for (int i = 0; i < parking.length(); i++) {
                String id = parking.getString(i);
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

    private Route(Parcel source) {
        this.id = source.readLong();
        this.name = source.readString();
        List<Coordinate> points = new LinkedList<>();
        source.readTypedList(points, Coordinate.CREATOR);
        this.points = Collections.unmodifiableList(points);
        List<String> parkingIds = new LinkedList<>();
        source.readStringList(parkingIds);
        this.parkingIds = Collections.unmodifiableList(parkingIds);
        this.departure = source.readParcelable(Coordinate.class.getClassLoader());
        this.destination = source.readParcelable(Coordinate.class.getClassLoader());
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
    public Coordinate getClostestOnLine(@NonNull Coordinate p) {
        if (this.points.size() < 1)
            return null;
        if (this.points.contains(p)) {
            return p;
        }
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
                closestPoint = pointOnLine;
            }

            a = b;
        }

        return closestPoint;
    }

    @Nullable
    public Coordinate getNextPoint(@NonNull Coordinate p) {
        if (this.points.size() < 1)
            return null;
        if (this.points.contains(p)) {
            return p;
        }
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
                // return null if the search point is past the destination
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

    public int getNextIndex(Coordinate coordinate) {
        return this.getIndexOfPoint(getNextPoint(coordinate));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(this.name);
        dest.writeTypedList(this.points);
        dest.writeStringList(parkingIds);
        dest.writeParcelable(departure, flags);
        dest.writeParcelable(destination, flags);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return id == route.id && name.equals(route.name) && points.equals(route.points) && parkingIds.equals(route.parkingIds) && departure.equals(route.departure) && destination.equals(route.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, points, parkingIds, departure, destination);
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }

        @Override
        public Route createFromParcel(Parcel source) {
            return new Route(source);
        }
    };
}
