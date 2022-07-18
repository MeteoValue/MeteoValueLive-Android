package de.jadehs.mvl.data.models.routing;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CurrentRouteETA implements Parcelable {

    @NonNull
    private final List<CurrentParkingETA> parkingETAs;
    @NonNull
    private final RouteETA destinationETA;
    @NonNull
    private final Route route;

    public CurrentRouteETA(@NonNull Route route, @NonNull List<CurrentParkingETA> parkingETAs, @NonNull RouteETA destinationETA) {
        this.parkingETAs = Collections.unmodifiableList(parkingETAs);
        this.destinationETA = destinationETA;
        this.route = route;
    }

    private CurrentRouteETA(Parcel source) {
        List<CurrentParkingETA> etas = new LinkedList<>();
        source.readTypedList(etas, CurrentParkingETA.CREATOR);
        parkingETAs = Collections.unmodifiableList(etas);
        destinationETA = source.readParcelable(RouteETA.class.getClassLoader());
        route = source.readParcelable(Route.class.getClassLoader());
    }


    @NonNull
    public List<CurrentParkingETA> getParkingETAs() {
        return parkingETAs;
    }

    @NonNull
    public RouteETA getDestinationETA() {
        return destinationETA;
    }

    @NonNull
    public Route getRoute() {
        return route;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentRouteETA that = (CurrentRouteETA) o;
        return parkingETAs.equals(that.parkingETAs) && destinationETA.equals(that.destinationETA) && route.equals(that.route);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parkingETAs, destinationETA, route);
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrentRouteETA{" +
                "parkingETAs=" + parkingETAs +
                ", destinationETA=" + destinationETA +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(parkingETAs);
        dest.writeParcelable(destinationETA, flags);
        dest.writeParcelable(route, flags);
    }

    public static final Creator<CurrentRouteETA> CREATOR = new Creator<CurrentRouteETA>() {
        @Override
        public CurrentRouteETA createFromParcel(Parcel source) {
            return new CurrentRouteETA(source);
        }

        @Override
        public CurrentRouteETA[] newArray(int size) {
            return new CurrentRouteETA[size];
        }
    };
}
