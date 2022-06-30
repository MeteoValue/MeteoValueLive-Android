package de.jadehs.mvl.data.parking.remote.routing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;

import de.jadehs.mvl.data.parking.models.Coordinate;

public class RouteRequest {

    public static RouteRequestBuilder newBuilder() {
        return new RouteRequestBuilder();
    }

    @NonNull
    private final DateTime starttime;
    @NonNull
    private final Coordinate from;
    @NonNull
    private final Coordinate to;
    @NonNull
    private final Vehicle vehicle;
    @Nullable
    private final List<Coordinate> via;

    public RouteRequest(@NonNull DateTime starttime, @NonNull Coordinate from, @NonNull Coordinate to, @NonNull Vehicle vehicle, @Nullable List<Coordinate> via) {
        this.starttime = starttime;
        this.from = from;
        this.to = to;
        this.vehicle = vehicle;
        if (via != null) {
            this.via = Collections.unmodifiableList(via);
        } else {
            this.via = null;
        }

    }

    public RouteRequest(@NonNull DateTime starttime, @NonNull Coordinate from, @NonNull Coordinate to, @NonNull Vehicle vehicle) {
        this.starttime = starttime;
        this.from = from;
        this.to = to;
        this.vehicle = vehicle;
        this.via = null;
    }

    @NonNull
    public DateTime getStarttime() {
        return starttime;
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

    public static class RouteRequestBuilder {


        private DateTime starttime;

        private Coordinate from;

        private Coordinate to;

        private Vehicle vehicle;
        @Nullable
        private List<Coordinate> via;

        protected RouteRequestBuilder() {

        }

        @NonNull
        public DateTime getStarttime() {
            return starttime;
        }

        public void setStarttime(@NonNull DateTime starttime) {
            this.starttime = starttime;
        }

        @NonNull
        public Coordinate getFrom() {
            return from;
        }

        public void setFrom(@NonNull Coordinate from) {
            this.from = from;
        }

        @NonNull
        public Coordinate getTo() {
            return to;
        }

        public void setTo(@NonNull Coordinate to) {
            this.to = to;
        }

        @NonNull
        public Vehicle getVehicle() {
            return vehicle;
        }

        public void setVehicle(@NonNull Vehicle vehicle) {
            this.vehicle = vehicle;
        }

        @Nullable
        public List<Coordinate> getVia() {
            return via;
        }

        public void setVia(@Nullable List<Coordinate> via) {
            this.via = via;
        }

        public RouteRequest build() {
            if (this.from == null || this.to == null || this.starttime == null || this.vehicle == null) {
                throw new IllegalStateException("from, to, starttime or vehicle is null and can't");
            }
            return new RouteRequest(this.starttime, this.from, this.to, this.vehicle, this.via);
        }
    }
}
