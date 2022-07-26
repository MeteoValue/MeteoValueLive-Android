package de.jadehs.mvl.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.jadehs.mvl.R
import de.jadehs.mvl.settings.MainSharedPreferences
import org.joda.time.DateTime
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder

class ResetDrivingTimeDialog : DialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(): ResetDrivingTimeDialog {
            return ResetDrivingTimeDialog()
        }
    }

    private lateinit var preference: MainSharedPreferences
    private lateinit var timeFormatter: PeriodFormatter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeFormatter =
            PeriodFormatterBuilder().printZeroAlways().minimumPrintedDigits(1).appendHours()
                .appendLiteral(":").minimumPrintedDigits(2).appendMinutes()
                .appendLiteral(" Stunden")
                .toFormatter()
        preference = MainSharedPreferences(requireContext())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val period = preference.maxTimeDriving
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.reset_driving_time_title)
            .setMessage(
                getString(
                    R.string.reset_driving_time_description,
                    timeFormatter.print(period)
                )
            )
            .setPositiveButton(android.R.string.ok) { _, _ ->
                preference.currentDrivingLimit = DateTime.now().plus(period)
            }.setNeutralButton(R.string.change) { _, _ ->
                PeriodDialog.newInstance(
                    getString(R.string.driving_time_dialog_title),
                    getString(R.string.driving_time_dialog_description),
                    period,
                    false
                ).show(parentFragmentManager, null)
            }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }.create()

    }

    override fun onDestroy() {
        super.onDestroy()
        preference.recycle()
    }
}