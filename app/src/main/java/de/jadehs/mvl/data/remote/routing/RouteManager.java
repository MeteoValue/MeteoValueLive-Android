package de.jadehs.mvl.data.remote.routing;

import androidx.annotation.NonNull;

import de.jadehs.mvl.data.models.routing.RouteETA;
import io.reactivex.rxjava3.core.Single;

public interface RouteManager {

    public Single<RouteETA> createRoute(@NonNull RouteRequest request);

    public Single<RouteETA> getETA(long id);
}
