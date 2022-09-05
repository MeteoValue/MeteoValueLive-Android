package de.jadehs.mvl.ui.tour_overview.recycler

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import de.jadehs.mvl.R
import de.jadehs.mvl.data.models.parking.Parking
import de.jadehs.mvl.data.models.parking.ParkingProperty
import de.jadehs.mvl.data.models.routing.CurrentParkingETA
import de.jadehs.mvl.data.models.routing.RouteETA
import de.jadehs.mvl.data.remote.routing.Vehicle
import de.jadehs.mvl.databinding.ParkingEtaListEntryBinding
import de.jadehs.mvl.utils.MultiplyColorFilter
import org.joda.time.DateTime
import org.joda.time.Period
import java.util.*
import java.util.function.Consumer

/**
 * Class which holds and controls the parking_eta_list_entry layout
 *
 * Intended to be used with ParkingEtaAdapter
 *
 *
 * @constructor root view of the parking_eta_list_entry layout
 */
class ParkingEtaViewHolder(
    view: View,
    private val onReportClickListener: Consumer<Parking>,
    private val drawableProvider: (resourceId: Int) -> Drawable,
    vehicleType: Vehicle
) :
    RecyclerView.ViewHolder(view) {

    private val binding: ParkingEtaListEntryBinding = ParkingEtaListEntryBinding.bind(view)
    private val occupancyString: String = view.context.getString(R.string.occupancy_of_spaces)
    private val etaString: String = view.context.getString(R.string.eta_hours)
    private val distanceString: String = view.context.getString(R.string.number_kilometers)

    private val warningBackgroundColor: ColorStateList
    private val goodBackgroundColor: ColorStateList

    private val warningColor: Int
    private val errorColor: Int

    private var lastParking: Parking? = null

    init {
        val context = view.context
        val colors = intArrayOf(
            R.attr.goodBackgroundColor, // 0
            R.attr.warningBackgroundColor, // 1
            R.attr.colorWarning, // 2
            R.attr.colorError, // 3
        )
        val attrs = context.obtainStyledAttributes(colors)
        val defaultColor = ColorStateList.valueOf(Color.WHITE)
        goodBackgroundColor = attrs.getColorStateList(0) ?: defaultColor
        warningBackgroundColor = attrs.getColorStateList(1) ?: defaultColor

        warningColor = attrs.getColor(2, Color.YELLOW)
        errorColor = attrs.getColor(3, Color.RED)
        attrs.recycle()


        binding.parkingOccupancyReportButton.setOnClickListener {
            lastParking?.let { parking ->
                onReportClickListener.accept(parking)
            }
        }
        setVehicleType(vehicleType)
    }

    /**
     * binds the given data to the view
     *
     * @param currentParkingETA the currentParkingETA instance
     * @param maxDrivingTime time in milliseconds from the point in time where the max driving time is reached
     */
    fun bind(currentParkingETA: CurrentParkingETA, distance: Double, maxDrivingTime: Long) {

        lastParking = currentParkingETA.parking
        val eta = if (distance <= 0) null else currentParkingETA.eta
        var etaWarningVisibility =
            arrivalAfterDrivingTime(eta, maxDrivingTime)
        val warningState = getOccupancyState(currentParkingETA)

        if (distance <= 0) {
            etaWarningVisibility = false
        }

        binding.parkingName.text = currentParkingETA.parking.name

        setETA(eta)
        setDistance(distance.toInt())
        setOccupancy(currentParkingETA)
        setParkingProperties(currentParkingETA.parking.properties)
        setETAWarningVisibility(etaWarningVisibility)
        setOccupancyWarningState(warningState)
        setBackgroundColor(
            getCardColor(
                currentParkingETA,
                warningState,
                etaWarningVisibility,
                distance
            )
        )


    }

    private fun setParkingProperties(properties: Array<ParkingProperty>) {
        binding.propertiesContainer.removeAllViews()
        if (binding.propertiesContainer.childCount > properties.size) {
            for (i in properties.size until binding.propertiesContainer.childCount) {
                binding.propertiesContainer.removeViewAt(i)
            }
        }
        properties.forEachIndexed { index, property ->

            val view: ImageView
            if (index < binding.propertiesContainer.childCount) {
                view = binding.propertiesContainer[index] as ImageView
            } else {
                view = ImageView(itemView.context)
                binding.propertiesContainer.addView(view)
            }
            view.apply {
                setImageDrawable(
                    when (property) {
                        ParkingProperty.RESTAURANT -> drawableProvider(R.drawable.ic_restaurant)
                        ParkingProperty.RESTROOM -> drawableProvider(R.drawable.ic_wc)
                        ParkingProperty.GAS_STATION -> drawableProvider(R.drawable.ic_local_gas_station)
                        ParkingProperty.SHOWER -> drawableProvider(R.drawable.ic_shower)
                        ParkingProperty.HOTEL -> drawableProvider(R.drawable.ic_local_hotel)
                    }
                )
            }
        }
    }

    private fun arrivalAfterDrivingTime(eta: RouteETA?, maxDrivingTime: Long): Boolean {
        if (eta == null)
            return false
        return eta.etaWeather.isAfter(maxDrivingTime)
    }

    private fun getOccupancyState(currentParkingETA: CurrentParkingETA): WarningState {
        val max = currentParkingETA.maxSpots
        val occupied = getOccupancy(currentParkingETA)
        val occupancyPercent = occupied / max.toDouble()

        if (occupancyPercent > 0.80)
            return WarningState.HIGH
        if (occupancyPercent > 0.50)
            return WarningState.MEDIUM
        return WarningState.NONE
    }

    private fun getCardColor(
        currentParkingETA: CurrentParkingETA,
        state: WarningState,
        arriveAfterDrivingTime: Boolean,
        distance: Double
    ): ColorStateList {
        if (currentParkingETA.eta == null || distance <= 0)
            return ColorStateList.valueOf(Color.GRAY)

        if (arriveAfterDrivingTime)
            return warningBackgroundColor
        return when (state) {
            WarningState.HIGH -> {
                warningBackgroundColor
            }
            else -> {
                goodBackgroundColor
            }
        }
    }

    fun setETA(eta: RouteETA?) {
        if (eta == null) {
            binding.parkingEta.text = "---"
            return
        }
        val travelTime = Period(DateTime.now(), eta.etaWeather)

        if (travelTime.hours < 0 || travelTime.minutes < 0) {
            binding.parkingEta.text = "??:??"
            return
        }
        binding.parkingEta.text = etaString.format(
            Locale.ROOT,
            travelTime.hours,
            travelTime.minutes
        )
    }

    fun setDistance(meters: Int) {
        if (meters <= 0) {
            binding.parkingDistance.text = "---"
            return
        }
        binding.parkingDistance.text = distanceString.format(
            Locale.ROOT,
            (meters / 1000.toDouble())
        )
    }

    fun setOccupancy(currentParkingETA: CurrentParkingETA) {
        val currentOccupancy = currentParkingETA.currentOccupiedSpots.occupied
        val destinationOccupancy = currentParkingETA.destinationOccupiedSpots



        binding.parkingOccupancy.text = occupancyString.format(
            Locale.ROOT,
            currentOccupancy,
            currentParkingETA.maxSpots
        )

        if (destinationOccupancy > 0) {
            binding.destinationParkingEta.text =
                "($destinationOccupancy/${currentParkingETA.maxSpots})"
        }
    }

    private fun getOccupancy(currentParkingETA: CurrentParkingETA) =
        if (currentParkingETA.destinationOccupiedSpots <= 0) {
            currentParkingETA.currentOccupiedSpots.occupied
        } else {
            val now = DateTime.now()
            val nextHour = now.plusHours(1)
            val weatherETA = currentParkingETA.eta?.etaWeather

            if (weatherETA?.isBefore(nextHour) == true)
                currentParkingETA.currentOccupiedSpots.occupied
            else
                currentParkingETA.destinationOccupiedSpots
        }

    fun setETAWarningVisibility(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        binding.parkingEtaWarningIcon.visibility = visibility
        binding.parkingEtaWarning.visibility = visibility
    }

    fun setOccupancyWarningState(state: WarningState) {
        val visibility = if (state == WarningState.NONE) View.GONE
        else {
            val color = if (state == WarningState.HIGH) errorColor else warningColor
            binding.parkingOccupancyWarningIcon.colorFilter = MultiplyColorFilter(color)

            // is assigned to val visibility
            View.VISIBLE
        }

        binding.parkingOccupancyWarningIcon.visibility = visibility
    }

    private fun setVehicleType(vehicleType: Vehicle) {
        binding.parkingDistanceIcon.setImageDrawable(
            when (vehicleType) {
                Vehicle.TRUCK -> {
                    drawableProvider(R.drawable.ic_truck)
                }
                Vehicle.BUS -> {
                    drawableProvider(R.drawable.ic_bus)
                }
            }
        )
    }


    private fun setBackgroundColor(color: ColorStateList) {
        binding.root.setCardBackgroundColor(color)
    }

    enum class WarningState {
        NONE, MEDIUM, HIGH
    }

}