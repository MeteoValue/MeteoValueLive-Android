package de.jadehs.mvl.data.models.routing;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.jadehs.mvl.data.models.Coordinate;
import de.jadehs.mvl.data.models.JsonSerializable;
import de.jadehs.mvl.data.remote.routing.Vehicle;

public class RouteETA implements Parcelable, JsonSerializable {

    public static DateTimeFormatter jsonDateFormatter = DateTimeFormat.forPattern("MMM dd, yyyy, h:mm:ss a")
            .withLocale(Locale.US)
            .withZone(DateTimeZone.forOffsetHours(2));

    public static RouteETA fromJSON(JSONObject object) throws JSONException {


        ViaList via = null;
        if (object.has("via")) {
            via = ViaList.fromString(object.getString("via"));
        }

        return new RouteETA(
                object.getBoolean("success"),
                object.getString("message"),
                object.getLong("id"),
                jsonDateFormatter.parseDateTime(object.getString("start")),
                jsonDateFormatter.parseDateTime(object.getString("eta")),
                jsonDateFormatter.parseDateTime(object.getString("etaWeather")),
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
        if (via != null && via.size() > 0) {
            this.via = via;
        } else {
            this.via = null;
        }

    }

    private RouteETA(Parcel source) {
        this.success = source.readInt() == 1;
        this.message = source.readString();
        this.id = source.readLong();
        this.start = new DateTime(source.readLong());
        this.eta = new DateTime(source.readLong());
        this.etaWeather = new DateTime(source.readLong());
        this.from = source.readParcelable(Coordinate.class.getClassLoader());
        this.to = source.readParcelable(Coordinate.class.getClassLoader());
        this.vehicle = Vehicle.fromInt(source.readInt());
        List<Coordinate> via = new LinkedList<>();
        source.readTypedList(via, Coordinate.CREATOR);
        if (via.size() > 0) {
            this.via = new ViaList(via);
        } else {
            this.via = null;
        }


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

    public Period getTravelTime() {
        return new Period(getStart(), getEta());
    }

    public Period getWeatherTravelTime() {
        return new Period(getStart(), getEtaWeather());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteETA routeETA = (RouteETA) o;
        return success == routeETA.success && id == routeETA.id && message.equals(routeETA.message) && start.equals(routeETA.start) && eta.equals(routeETA.eta) && etaWeather.equals(routeETA.etaWeather) && from.equals(routeETA.from) && to.equals(routeETA.to) && vehicle == routeETA.vehicle && Objects.equals(via, routeETA.via);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, id, start, eta, etaWeather, from, to, vehicle, via);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(success ? 1 : 0);
        dest.writeString(message);
        dest.writeLong(id);
        dest.writeLong(start.getMillis());
        dest.writeLong(eta.getMillis());
        dest.writeLong(etaWeather.getMillis());
        dest.writeParcelable(from, flags);
        dest.writeParcelable(to, flags);
        dest.writeInt(vehicle.getId());
        dest.writeTypedList(via);
    }

    @Override
    public Object toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("success", success);
        object.put("message", message);
        object.put("id", id);
        object.put("start", jsonDateFormatter.print(start));
        object.put("eta", jsonDateFormatter.print(eta));
        object.put("etaWeather", jsonDateFormatter.print(etaWeather));
        object.put("from", from.toSimpleString());
        object.put("to", to.toSimpleString());
        object.put("vehicle", vehicle.toString());
        if (via != null) {
            object.put("via", via.toString());
        }
        return object;
    }


    public static final Creator<RouteETA> CREATOR = new Creator<RouteETA>() {
        @Override
        public RouteETA createFromParcel(Parcel source) {
            return new RouteETA(source);
        }

        @Override
        public RouteETA[] newArray(int size) {
            return new RouteETA[size];
        }
    };


}
