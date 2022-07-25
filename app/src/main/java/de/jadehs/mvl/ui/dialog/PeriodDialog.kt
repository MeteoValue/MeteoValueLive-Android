package de.jadehs.mvl.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import de.jadehs.mvl.R
import de.jadehs.mvl.utils.getPeriod
import de.jadehs.mvl.utils.setPeriod
import org.joda.time.Duration
import org.joda.time.Period


class PeriodDialog : DialogFragment() {

    companion object {

        @JvmStatic
        fun newInstance(title: String, description: String, period: Period?): PeriodDialog {
            return PeriodDialog().apply {
                arguments = newArguments(title, description, period)
            }
        }

        @JvmStatic
        fun newArguments(title: String, description: String, period: Period?): Bundle {
            return Bundle().apply {
                putString(ARGUMENT_TITLE, title)
                putString(ARGUMENT_DESCRIPTION, description)
                period?.let {
                    putLong(ARGUMENT_DEFAULT_PERIOD, it.toStandardDuration().millis)
                }

            }
        }

        const val REQUEST_CODE = "de.jadehs.mvl.period_dialog"

        const val ARGUMENT_TITLE = "de.jadehs.mvl.period_dialog.title"

        const val ARGUMENT_DESCRIPTION = "de.jadehs.mvl.period_dialog.description"

        const val ARGUMENT_DEFAULT_PERIOD = "de.jadehs.mvl.period_dialog.default"

        /**
         * result value as long
         */
        const val RESULT_DRIVING_TIME =
            "de.jadehs.mvl.period_dialog.result"
    }

    private lateinit var description: String
    private lateinit var title: String
    private lateinit var period: Period

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        period =
            Period(arguments!!.getLong(ARGUMENT_DEFAULT_PERIOD, Duration.standardHours(5).millis))
        title = arguments!!.getString(ARGUMENT_TITLE, "")
        description = arguments!!.getString(ARGUMENT_DESCRIPTION, "")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val v = layoutInflater.inflate(R.layout.preference_time, null)
        val picker = v.findViewById<TimePicker>(R.id.preference_time_picker)
        picker.setIs24HourView(true)
        picker.setPeriod(period)


        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val period = picker.getPeriod()

                sendResult(period)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setView(v)
            .create()
    }


    private fun sendResult(period: Period) {
        this.setFragmentResult(REQUEST_CODE, Bundle().apply {
            putLong(
                RESULT_DRIVING_TIME, period.toStandardDuration().millis
            )
        })
    }


}