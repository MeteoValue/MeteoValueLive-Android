package de.jadehs.mvl.data.models;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonSerializable {

    Object toJson() throws JSONException;
}
