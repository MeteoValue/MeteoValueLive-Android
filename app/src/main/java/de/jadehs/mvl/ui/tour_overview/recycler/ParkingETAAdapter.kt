package de.jadehs.mvl.ui.tour_overview.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import de.jadehs.mvl.R
import de.jadehs.mvl.data.models.routing.CurrentParkingETA

class ParkingETAAdapter :
    ListAdapter<CurrentParkingETA, ParkingEtaViewHolder>(ParkingETADiffer()) {

    val maxDrivingTime: Long = Long.MAX_VALUE


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingEtaViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        return ParkingEtaViewHolder(
            inflator.inflate(
                R.layout.parking_eta_list_entry,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ParkingEtaViewHolder, position: Int) {
        holder.bind(this.getItem(position), maxDrivingTime)
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