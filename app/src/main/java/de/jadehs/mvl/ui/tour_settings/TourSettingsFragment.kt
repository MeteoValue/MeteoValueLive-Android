package de.jadehs.mvl.ui.tour_settings

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import de.jadehs.mvl.R
import de.jadehs.mvl.data.models.routing.Route
import de.jadehs.mvl.databinding.FragmentTourSettingsBinding
import de.jadehs.mvl.ui.tour_overview.TourOverviewFragment
import de.jadehs.mvl.utils.getPeriod
import de.jadehs.mvl.utils.setHourCompat
import de.jadehs.mvl.utils.setMinuteCompat
import de.jadehs.mvl.utils.setPeriod
import org.joda.time.DateTime
import org.joda.time.Period


class TourSettingsFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = TourSettingsFragment()
    }

    private var _binding: FragmentTourSettingsBinding? = null

    /**
     * This property is only valid between onCreateView and
     * onDestroyView.
     */
    private val binding: FragmentTourSettingsBinding
        get() = _binding!!

    private lateinit var viewModel: TourSettingsViewModel
    private lateinit var routeAdapter: ArrayAdapter<Route>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TourSettingsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTourSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.preferences.currentlyDriving?.let {
            Navigation.findNavController(requireView())
                .navigate(
                    R.id.action_nav_tour_settings_to_nav_tour_overview,
                    TourOverviewFragment.newInstanceBundle(it)
                )
        }

        setupSpinner()
        setupTimePicker()
        setupContinueButton()

        setupObserver()
    }

    // region setup views
    private fun setupContinueButton() {
        binding.continueButton.setOnClickListener {
            val route = getSelectedRoute()
            if (route == null) {
                Toast.makeText(requireContext(), R.string.pls_select_route, Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            viewModel.preferences.currentDrivingLimit =
                DateTime.now().plus(binding.tourSettingsTimePicker.getPeriod())

            Navigation.findNavController(requireView())
                .navigate(
                    R.id.action_nav_tour_settings_to_nav_tour_overview,
                    TourOverviewFragment.newInstanceBundle(route.id)
                )
        }
    }

    private fun setupTimePicker() {
        val timePicker = binding.tourSettingsTimePicker
        timePicker.setIs24HourView(true)
        val period = viewModel.preferences.maxTimeDriving
        binding.tourSettingsTimePicker.setPeriod(period)
    }

    private fun setupObserver() {
        viewModel.allRoutes.observe(viewLifecycleOwner) { routes ->
            routes?.let {
                routeAdapter.clear()
                routeAdapter.addAll(routes)
            }
        }
    }

    private fun setupSpinner() {
        val spinner = binding.tourSettingsDestinationSpinner

        (spinner.editText as AutoCompleteTextView).apply {
            routeAdapter = ArrayAdapter<Route>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            this.setAdapter(routeAdapter)
            this.setOnItemClickListener { _, _, _, _ ->
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).let {
                    it.hideSoftInputFromWindow(this.windowToken, 0)
                    this.clearFocus()
                }
            }
        }
    }

    // endregion setup views

    private fun getSelectedRoute(): Route? {
        val routes = viewModel.allRoutes.value
        val selectedRouteName = binding.tourSettingsDestinationSpinner.editText?.text.toString()

        return routes?.find { route -> route.name.equals(selectedRouteName, true) }
    }

}