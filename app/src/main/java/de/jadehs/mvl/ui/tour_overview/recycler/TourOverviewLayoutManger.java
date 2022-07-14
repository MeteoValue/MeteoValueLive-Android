package de.jadehs.mvl.ui.tour_overview.recycler;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TourOverviewLayoutManger extends LinearLayoutManager {


    private Runnable onLayoutCompleted;


    public TourOverviewLayoutManger(Context context) {
        super(context, RecyclerView.VERTICAL, false);
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (onLayoutCompleted != null) {
            onLayoutCompleted.run();
        }
    }

    public void setOnLayoutCompleted(Runnable onLayoutCompleted) {
        this.onLayoutCompleted = onLayoutCompleted;
    }
}
