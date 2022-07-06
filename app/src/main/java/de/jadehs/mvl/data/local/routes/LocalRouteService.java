package de.jadehs.mvl.data.local.routes;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import de.jadehs.mvl.data.RouteService;
import de.jadehs.mvl.data.local.ContextClient;
import de.jadehs.mvl.data.models.routing.Route;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LocalRouteService extends ContextClient implements RouteService {

    public static final String ROUTE_FOLDER = "routes/";
    public static final String ROUTE_SUFFIX = "coords.json";

    public LocalRouteService(@NonNull Context context) {
        super(context);
    }

    @Override
    public Single<List<Route>> getAllRoutes() {
        return getRouteFileNames()
                .map(this::readFile)
                .map(JSONObject::new)
                .map(Route::fromJson)
                .toList()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Route> getRoute(long id) {
        return getRouteFileNames()
                .filter((file) -> file.startsWith(Long.toString(id)))
                .firstOrError()
                .map(this::readFile)
                .map(JSONObject::new)
                .map(Route::fromJson)
                .subscribeOn(Schedulers.io());
    }


    private Flowable<String> getRouteFileNames() {
        return Flowable.just(getAssets())
                .map((assetManager -> assetManager.list(ROUTE_FOLDER)))
                .flatMap(Flowable::fromArray)
                .filter(file -> file.endsWith(ROUTE_SUFFIX));
    }

    private String readFile(String filename) throws IOException {
        try (InputStream stream = getAssets().open(ROUTE_FOLDER + filename)) {
            StringBuilder fileString = new StringBuilder();
            String line;
            while ((line = new BufferedReader(new InputStreamReader(stream)).readLine()) != null) {
                fileString.append(line);
                fileString.append("\n");
            }
            return fileString.toString();
        }

    }
}
