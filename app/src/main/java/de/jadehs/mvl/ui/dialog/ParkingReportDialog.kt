package de.jadehs.mvl.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.jadehs.mvl.R
import de.jadehs.mvl.data.models.parking.Parking
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.tabs.TabLayout
import de.jadehs.mvl.data.models.parking.ParkingOccupancyReport
import org.joda.time.DateTime
import java.util.*

/**
 * Shows a dialog to rate the occupancy of a given parking space
 *
 * Does publish the Dialog result by calling [FragmentManager.setFragmentResult] with the [REQUEST_CODE] as request code
 * and the occupancy with the key [RESULT_PARKING_OCCUPANCY_REPORT]
 */
class ParkingReportDialog : DialogFragment() {

    companion object {

        @JvmStatic
        fun newInstance(parking: Parking): ParkingReportDialog {
            return ParkingReportDialog().apply {
                arguments = newArguments(parking)
            }
        }

        @JvmStatic
        fun newInstance(id: String, name: String): ParkingReportDialog {
            return ParkingReportDialog().apply {
                arguments = newArguments(id, name)
            }
        }

        @JvmStatic
        fun newArguments(parking: Parking): Bundle {
            return newArguments(parking.id, parking.name)
        }

        @JvmStatic
        fun newArguments(id: String, name: String): Bundle {
            return Bundle().apply {
                putString(EXTRA_PARKING_NAME, name)
                putString(EXTRA_PARKING_ID, id)
            }

        }

        const val REQUEST_CODE = "de.jadehs.mvl.parking_report_dialog"

        /**
         * result value as Parcelable of [ParkingOccupancyReport]
         */
        const val RESULT_PARKING_OCCUPANCY_REPORT =
            "de.jadehs.mvl.parking_report_dialog.parking_occupancy"


        /**
         * name of the parking location
         */
        const val EXTRA_PARKING_NAME = "de.jadehs.mvl.parking_report_idalog.parking_name"

        /**
         * id of the parking location
         */
        const val EXTRA_PARKING_ID = "de.jadehs.mvl.parking_report_idalog.parking_id"
    }


    private lateinit var titleString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        titleString = getString(R.string.parking_occupancy_report_dialog_title)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val name = arguments?.getString(EXTRA_PARKING_NAME)!!
        val id = arguments?.getString(EXTRA_PARKING_ID)!!

        return AlertDialog.Builder(requireContext())
            .setTitle(titleString.format(Locale.ROOT, name))
            .setMessage(R.string.parking_occupancy_report_dialog_description)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                val tabs: TabLayout = (dialog as Dialog).findViewById(R.id.parking_occupancy_picker)
                val occupancy = when (tabs.selectedTabPosition) {
                    0 -> {
                        ParkingOccupancyReport.ParkingOccupancy.HIGHER
                    }
                    1 -> {
                        ParkingOccupancyReport.ParkingOccupancy.HIGH
                    }
                    2 -> {
                        ParkingOccupancyReport.ParkingOccupancy.MEDIUM
                    }
                    3 -> {
                        ParkingOccupancyReport.ParkingOccupancy.LOW
                    }
                    else -> {
                        null
                    }
                }
                val occupancyReport = occupancy?.run {
                    ParkingOccupancyReport(id, DateTime.now(), occupancy)
                }
                occupancyReport?.let {
                    sendResult(it)
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            .setView(R.layout.dialog_parking_report)
            .create()
    }


    fun sendResult(occupancyReport: ParkingOccupancyReport) {
        this.setFragmentResult(REQUEST_CODE, Bundle().apply {
            putParcelable(
                RESULT_PARKING_OCCUPANCY_REPORT, occupancyReport
            )
        })
    }


}