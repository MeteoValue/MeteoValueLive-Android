package de.jadehs.mvl.data.models.routing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.remote.routing.Vehicle;

public class RouteETA {

    public static RouteETA fromJSON(JSONObject object) throws JSONException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd, yyyy, h:mm:ss a").withLocale(Locale.US);

        ViaList via = null;
        if (object.has("via")) {
            via = ViaList.fromString(object.getString("via"));
        }

        return new RouteETA(
                object.getBoolean("success"),
                object.getString("message"),
                object.getLong("id"),
                formatter.parseDateTime(object.getString("start")),
                formatter.parseDateTime(object.getString("eta")),
                formatter.parseDateTime(object.getString("etaWeather")),
                Coordinate.fromSimpleString(object.getString("from")),
                Coordinate.fromSimpleString(object.getString("to")),
                Vehicle.valueOf(object.getString("vehicle").toUpperCase(Locale.ROOT)),
                via);
    }

    private final boolean success;
    @NonNull
    private final String message;
    private final long id;
    @NonNull
    private final DateTime start;
    @NonNull
    private final DateTime eta;
    @NonNull
    private final DateTime etaWeather;
    @NonNull
    private final Coordinate from;
    @NonNull
    private final Coordinate to;
    @NonNull
    private final Vehicle vehicle;
    @Nullable
    private final ViaList via;

    public RouteETA(boolean success, @NonNull String message, long id, @NonNull DateTime start, @NonNull DateTime eta, @NonNull DateTime etaWeather, @NonNull Coordinate from, @NonNull Coordinate to, @NonNull Vehicle vehicle, @Nullable ViaList via) {
        this.success = success;
        this.message = message;
        this.id = id;
        this.start = start;
        this.eta = eta;
        this.etaWeather = etaWeather;
        this.from = from;
        this.to = to;
        this.vehicle = vehicle;
        this.via = via;
    }

    public boolean isSuccess() {
        return success;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public DateTime getStart() {
        return start;
    }

    @NonNull
    public DateTime getEta() {
        return eta;
    }

    @NonNull
    public DateTime getEtaWeather() {
        return etaWeather;
    }

    @NonNull
    public Coordinate getFrom() {
        return from;
    }

    @NonNull
    public Coordinate getTo() {
        return to;
    }

    @NonNull
    public Vehicle getVehicle() {
        return vehicle;
    }

    @Nullable
    public List<Coordinate> getVia() {
        return via;
    }

    @NonNull
    @Override
    public String toString() {
        return "RouteETA{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", id=" + id +
                ", start=" + start +
                ", eta=" + eta +
                ", etaWeather=" + etaWeather +
                ", from=" + from +
                ", to=" + to +
                ", vehicle=" + vehicle +
                ", via=" + via +
                '}';
    }
}
