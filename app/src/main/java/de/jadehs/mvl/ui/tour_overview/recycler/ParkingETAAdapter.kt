package de.jadehs.mvl.ui.tour_overview.recycler

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import de.jadehs.mvl.R
import de.jadehs.mvl.data.models.Coordinate
import de.jadehs.mvl.data.models.parking.Parking
import de.jadehs.mvl.data.models.routing.CurrentParkingETA
import de.jadehs.mvl.data.models.routing.Route
import de.jadehs.mvl.data.remote.routing.Vehicle
import de.jadehs.mvl.utils.DistanceHelper
import java.util.function.Consumer

class ParkingETAAdapter(var vehicle: Vehicle) :
    ListAdapter<CurrentParkingETA, ParkingEtaViewHolder>(ParkingETADiffer()) {

    var maxDrivingTime: Long = Long.MAX_VALUE
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var route: Route? = null
        set(value) {
            field = value
            distanceCache.clear()
            notifyDataSetChanged()
        }

    var currentLocation: Coordinate? = null
        set(value) {
            field = value
            distanceCache.clear()
            notifyDataSetChanged()
        }

    private val distanceCache: MutableMap<CurrentParkingETA, Double> = HashMap()

    private var iconCache: MutableMap<Int, Drawable> = HashMap()

    /**
     * is called when a new list is applied
     */
    private var _onCurrentListChangedCallback: Consumer<List<CurrentParkingETA>>? = null

    /**
     * is called when a new list is applied
     */
    private var _onReportClickListener: Consumer<Parking>? = null

    fun setOnReportClickListener(callback: Consumer<Parking>) {
        _onReportClickListener = callback
    }

    fun setOnCurrentListChangedCallback(callback: Consumer<List<CurrentParkingETA>>) {
        _onCurrentListChangedCallback = callback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingEtaViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val resources = parent.resources
        val theme = parent.context.theme
        return ParkingEtaViewHolder(
            inflater.inflate(
                R.layout.parking_eta_list_entry,
                parent,
                false
            ),
            this::onReportClick,
            { id ->
                getCachedResource(id, resources, theme)
            },
            vehicle
        )
    }

    override fun onBindViewHolder(holder: ParkingEtaViewHolder, position: Int) {
        val item = this.getItem(position)
        var distance = distanceCache[item]
        if (distance == null) {
            route?.let { r ->
                item.eta?.let { eta ->
                    currentLocation?.let { loc ->
                        distance = DistanceHelper.getDistanceFromToRoute(r, loc, eta.to)
                    }
                }
            }

        }
        holder.bind(item, distance ?: -1.0, maxDrivingTime)
    }

    override fun onCurrentListChanged(
        previousList: MutableList<CurrentParkingETA>,
        currentList: MutableList<CurrentParkingETA>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        _onCurrentListChangedCallback?.accept(currentList)
    }

    private fun getCachedResource(
        resourceId: Int,
        resources: Resources,
        theme: Resources.Theme
    ): Drawable {
        return iconCache.computeIfAbsent(resourceId) { id ->
            resources.getDrawable(id, theme)
        }
    }

    private fun onReportClick(parking: Parking) {
        _onReportClickListener?.accept(parking)
    }

    class ParkingETADiffer : DiffUtil.ItemCallback<CurrentParkingETA>() {
        override fun areItemsTheSame(
            oldItem: CurrentParkingETA,
            newItem: CurrentParkingETA
        ): Boolean {
            return oldItem.parking.id.equals(newItem.parking.id)
        }

        override fun areContentsTheSame(
            oldItem: CurrentParkingETA,
            newItem: CurrentParkingETA
        ): Boolean {
            if (oldItem.parking.name != newItem.parking.name)
                return false


            if (oldItem.maxSpots != newItem.maxSpots)
                return false


            if (oldItem.destinationOccupiedSpots != newItem.destinationOccupiedSpots)
                return false


            if (newItem.destinationOccupiedSpots == -1 &&
                oldItem.currentOccupiedSpots.occupied != newItem.currentOccupiedSpots.occupied
            )
                return false


            val oldEta = oldItem.eta
            val newEta = newItem.eta
            if (oldEta != null) {
                if (newEta == null) {
                    return false
                } else {
                    // oldETa and newEta are non-null

                    if (oldEta.weatherTravelTime != newEta.weatherTravelTime)
                        return false

                    // TODO check if maxDrivingTime is necessary
                    /*if (
                        oldEta.etaWeather.isAfter(maxDrivingTime)
                        != newEta.etaWeather.isAfter(maxDrivingTime)
                    )
                        return false*/
                }


            } else {
                if (newEta != null)
                    return false
            }

            return true

        }

    }

}