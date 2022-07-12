package de.jadehs.mvl.data.remote.routing;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum Vehicle {
    TRUCK(0), BUS(1);

    @IntRange(from = 0, to = 1)
    private final int id;

    Vehicle(@IntRange(from = 0, to = 1) int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    @NonNull
    private static final Map<Integer, Vehicle> idMap = new HashMap<>();

    static {
        for (Vehicle v : Vehicle.values()) {
            idMap.put(v.id, v);
        }
    }

    public static Vehicle fromInt(@IntRange(from = 0, to = 1) int id) {
        return idMap.get(id);
    }


}
