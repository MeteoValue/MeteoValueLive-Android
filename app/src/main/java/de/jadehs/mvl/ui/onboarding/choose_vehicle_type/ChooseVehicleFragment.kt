package de.jadehs.mvl.ui.onboarding.choose_vehicle_type

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import de.jadehs.mvl.interfaces.Launcher
import de.jadehs.mvl.R
import de.jadehs.mvl.data.remote.routing.Vehicle
import de.jadehs.mvl.settings.MainSharedPreferences


/**
 * Fragment which shows a spinner to choose the current vehicle type.
 *
 * Used when the app is started for the first time
 */
class ChooseVehicleFragment : Fragment() {


    private lateinit var viewModel: ChooseVehicleViewModel
    private lateinit var preferences: MainSharedPreferences
    private lateinit var spinner: TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences =
            MainSharedPreferences(requireContext())
        viewModel = ViewModelProvider(this).get(ChooseVehicleViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_choose_vehicle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner(view, savedInstanceState)

        setupNextButton(view, savedInstanceState)

    }

    private fun setupNextButton(view: View, savedInstanceState: Bundle?) {
        val button: Button = view.findViewById(R.id.choose_type_next_button)


        button.setOnClickListener { _ ->
            val pos = viewModel.vehicleType.value

            if (pos in 0..1) {
                preferences.vehicleType = Vehicle.fromInt(pos!!)
                preferences.introDone = true

                val activity = requireActivity()

                if (activity is Launcher)
                    activity.startMain()
            } else {
                Snackbar.make(view, R.string.choose_vehicle_error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun setupSpinner(view: View, savedInstanceState: Bundle?) {
        spinner = view.findViewById(R.id.choose_type_spinner)
        viewModel.vehicleType.observe(viewLifecycleOwner) { pos ->
            val text = spinner.editText as AutoCompleteTextView
            val item = text.adapter.getItem(pos)
            text.setText(item.toString(), false)
        }
        (spinner.editText as AutoCompleteTextView).apply {
            this.setAdapter(
                ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.vehicle_types,
                    android.R.layout.simple_spinner_dropdown_item
                )
            )

            this.setOnItemClickListener { parent, view, position, id ->
                viewModel.setVehicleType(position)

            }
        }
    }
}
