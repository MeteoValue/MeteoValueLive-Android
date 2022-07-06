package de.jadehs.mvl.data.local;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;

public abstract class ContextClient {

    @NonNull
    private final Context context;

    public ContextClient(@NonNull Context context) {
        this.context = context;
    }


    @NonNull
    protected Context getContext() {
        return this.context;
    }


    protected AssetManager getAssets() {
        return this.getContext().getAssets();
    }
}
