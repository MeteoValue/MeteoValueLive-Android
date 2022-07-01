package de.jadehs.mvl.data;

import java.util.List;

import de.jadehs.mvl.data.models.routing.Route;
import io.reactivex.rxjava3.core.Single;

public interface RouteManager {


    Single<List<Route>> getAllRoutes();
}
