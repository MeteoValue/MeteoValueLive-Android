package de.jadehs.mvl.ui.tour_overview.recycler

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import de.jadehs.mvl.R
import de.jadehs.mvl.data.models.routing.CurrentParkingETA
import de.jadehs.mvl.data.models.routing.RouteETA
import de.jadehs.mvl.databinding.ParkingEtaListEntryBinding
import java.util.*

class ParkingEtaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding: ParkingEtaListEntryBinding = ParkingEtaListEntryBinding.bind(view)
    private val occupancyString: String = view.context.getString(R.string.occupancy_of_spaces)
    private val etaString: String = view.context.getString(R.string.eta_hours)
    private val distanceString: String = view.context.getString(R.string.number_kilometers)

    /**
     * binds the given data to the view
     *
     * @param currentParkingETA the currentParkingETA instance
     * @param maxDrivingTime time in milliseconds from the point in time where the max driving time is reached
     */
    fun bind(currentParkingETA: CurrentParkingETA, maxDrivingTime: Long) {
        binding.parkingName.setText(currentParkingETA.parking.name)
        setETA(currentParkingETA.eta)
        setDistance(currentParkingETA.distance.toInt())
        setOccupancy(currentParkingETA)
        val arrivalAfterDriving =
            arrivalAfterDrivingTime(currentParkingETA.eta, maxDrivingTime)
        val warningState = getOccupancyState(currentParkingETA)
        setETAWarningVisibility(arrivalAfterDriving)
        setOccupancyWarningState(warningState)
        setBackgroundColor(getCardColor(warningState, arrivalAfterDriving))
    }

    private fun arrivalAfterDrivingTime(eta: RouteETA?, maxDrivingTime: Long): Boolean {
        if (eta == null)
            return false
        return eta.etaWeather.isAfter(maxDrivingTime)
    }

    private fun getOccupancyState(currentParkingETA: CurrentParkingETA): WarningState {
        val max = currentParkingETA.maxSpots
        val occupied = if (currentParkingETA.destinationOccupiedSpots <= 0)
            currentParkingETA.currentOccupiedSpots.occupied
        else
            currentParkingETA.destinationOccupiedSpots
        val occupancyPercent = occupied / max.toDouble()

        if (occupancyPercent > 0.80)
            return WarningState.HIGH
        if (occupancyPercent > 0.50)
            return WarningState.MEDIUM
        return WarningState.NONE
    }

    private fun getCardColor(state: WarningState, arriveAfterDrivingTime: Boolean): Int {
        if (arriveAfterDrivingTime)
            return Color.RED
        return when (state) {
            WarningState.HIGH -> {
                Color.RED
            }
            else -> {
                Color.TRANSPARENT
            }
        }


    }

    fun setETA(eta: RouteETA?) {
        if (eta == null) {
            binding.parkingEta.setText("---")
            return
        }
        val travelTime = eta.weatherTravelTime
        binding.parkingEta.setText(
            String.format(
                Locale.ROOT,
                etaString,
                travelTime.hours,
                travelTime.minutes
            )
        )
    }

    fun setDistance(meters: Int) {
        if (meters <= 0) {
            binding.parkingDistance.setText("---")
            return
        }
        binding.parkingDistance.setText(
            String.format(
                Locale.ROOT,
                distanceString,
                (meters / 10)
            )
        )
    }

    fun setOccupancy(currentParkingETA: CurrentParkingETA) {
        val occupied =
            if (currentParkingETA.destinationOccupiedSpots <= 0)
                currentParkingETA.currentOccupiedSpots.occupied
            else
                currentParkingETA.destinationOccupiedSpots
        binding.parkingOccupancy.setText(
            String.format(
                Locale.ROOT,
                occupancyString,
                occupied,
                currentParkingETA.maxSpots
            )
        )
    }

    fun setETAWarningVisibility(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        binding.parkingEtaWarningIcon.visibility = visibility
        binding.parkingEtaWarning.visibility = visibility
    }

    fun setOccupancyWarningState(state: WarningState) {
        val visibility = if (state == WarningState.NONE) View.GONE
        else {
            val color = if (state == WarningState.HIGH) Color.RED else Color.YELLOW
            val colorStateList = ColorStateList.valueOf(color)

            binding.parkingOccupancyWarningIcon.imageTintList = colorStateList
            // is assigned to val visibility
            View.VISIBLE
        }

        binding.parkingOccupancyWarningIcon.visibility = visibility
    }



    private fun setBackgroundColor(color: Int) {
        // TODO fix colors
        val c = ColorUtils.setAlphaComponent(color, 50)
        binding.root.setCardBackgroundColor(c)
    }

    enum class WarningState {
        NONE, MEDIUM, HIGH
    }

}