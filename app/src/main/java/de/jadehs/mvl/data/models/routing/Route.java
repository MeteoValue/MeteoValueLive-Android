package de.jadehs.mvl.data.models.routing;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Deque;
import java.util.LinkedList;

import de.jadehs.mvl.data.models.Coordinate;

public class Route {

    public static Route fromJson(JSONObject object) throws JSONException {
        JSONObject routeMetadata = object.getJSONObject("route");
        JSONArray points = object.getJSONArray("points");
        Deque<Coordinate> pointList = new LinkedList<>();

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
    private final Deque<Coordinate> points;


    public Route(long id, @NonNull String name, @NonNull Deque<Coordinate> points) {
        this.id = id;
        this.name = name;
        this.points = points;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public Deque<Coordinate> getPoints() {
        return points;
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
