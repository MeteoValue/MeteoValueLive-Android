package de.jadehs.mvl.data.models.routing;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.jadehs.mvl.data.models.JsonSerializable;

public class CurrentRouteETAReport implements JsonSerializable {

    @NonNull
    private final UUID id;
    @NonNull
    private final CurrentRouteETA eta;

    public CurrentRouteETAReport(@NonNull CurrentRouteETA eta) {
        this.id = UUID.randomUUID();
        this.eta = eta;
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    @NonNull
    public CurrentRouteETA getETA() {
        return eta;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id.toString());
        jsonObject.put("routeETA", eta.toJson());
        return jsonObject;
    }
}
