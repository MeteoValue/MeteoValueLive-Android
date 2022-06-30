package de.jadehs.mvl.data.parking.remote.routing;

import androidx.annotation.NonNull;

import de.jadehs.mvl.data.parking.models.routing.RouteETA;
import io.reactivex.rxjava3.core.Single;

public interface RouteManager {

    public Single<RouteETA> createRoute(@NonNull RouteRequest request);

    public Single<RouteETA> getETA(long id);
}
