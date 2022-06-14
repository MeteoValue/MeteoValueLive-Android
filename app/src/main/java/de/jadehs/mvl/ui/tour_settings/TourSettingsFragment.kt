package de.jadehs.mvl.ui.tour_settings

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TimePicker
import com.google.android.material.textfield.TextInputLayout
import de.jadehs.mvl.R
import de.jadehs.mvl.settings.MainSharedPreferences

class TourSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = TourSettingsFragment()
    }

    private lateinit var timePicker: TimePicker
    private lateinit var spinner: TextInputLayout
    private lateinit var viewModel: TourSettingsViewModel
    private lateinit var preferences: MainSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tour_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner = view.findViewById(R.id.tour_settings_destination_spinner)
        timePicker = view.findViewById(R.id.tour_settings_time_picker)
        setupSpinner(view, savedInstanceState)
        setupTimePicker(view, savedInstanceState)
    }


    private fun setupTimePicker(view: View, savedInstanceState: Bundle?) {
        timePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = 0
            timePicker.minute = 0
        } else {
            timePicker.currentHour = 0
            timePicker.currentMinute = 0
        }

    }

    private fun setupSpinner(view: View, savedInstanceState: Bundle?) {
        val destinations = listOf("Düsseldorf", "Köln", "Palma")

        (spinner.editText as AutoCompleteTextView).apply {
            this.setAdapter(ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                destinations
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            })
            this.setOnItemClickListener { parent, view, position, id ->
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).let {
                    it.hideSoftInputFromWindow(this.windowToken,0)
                    this.clearFocus()
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TourSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}