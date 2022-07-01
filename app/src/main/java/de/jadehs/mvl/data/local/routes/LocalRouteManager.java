package de.jadehs.mvl.data.local.routes;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.jadehs.mvl.data.RouteManager;
import de.jadehs.mvl.data.local.ContextClient;
import de.jadehs.mvl.data.models.routing.Route;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LocalRouteManager extends ContextClient implements RouteManager {

    public static final String ROUTE_FOLDER = "routes/";
    public static final String ROUTE_SUFFIX = "coords.json";

    public LocalRouteManager(@NonNull Context context) {
        super(context);
    }

    @Override
    public Single<List<Route>> getAllRoutes() {
        return Single.fromCallable(() -> {
            AssetManager assets = getAssets();

            String[] files = assets.list(ROUTE_FOLDER);
            List<Route> routes = new ArrayList<>();
            for (String file :
                    files) {
                if (file.endsWith(ROUTE_SUFFIX)) {
                    InputStream stream = assets.open(ROUTE_FOLDER + file);
                    StringBuilder fileString = new StringBuilder();
                    String line;
                    while ((line = new BufferedReader(new InputStreamReader(stream)).readLine()) != null) {
                        fileString.append(line);
                        fileString.append("\n");
                    }

                    routes.add(Route.fromJson(new JSONObject(fileString.toString())));
                }
            }
            return routes;
        }).subscribeOn(Schedulers.io());
    }
}
