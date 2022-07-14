package de.jadehs.mvl.ui.tour_overview.recycler

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

class ToStartSmoothScroller(context: Context) : LinearSmoothScroller(context) {
    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }

    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_START
    }
}