package de.jadehs.mvl.ui.tour_overview

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import de.jadehs.mvl.databinding.FragmentTourOverviewBinding
import de.jadehs.mvl.ui.tour_overview.recycler.ParkingETAAdapter

class TourOverviewFragment : Fragment() {

    companion object {
        const val ARG_ROUTE_ID = "de.jadehs.mvl.TourOverviewFragment.route_id"

        @JvmStatic
        fun newInstance(routeId: Long): TourOverviewFragment {
            return TourOverviewFragment().apply {
                arguments = newInstanceBundle(routeId)
            }
        }

        @JvmStatic
        fun newInstanceBundle(routeId: Long): Bundle {
            val b = Bundle()
            b.putLong(ARG_ROUTE_ID, routeId)
            return b
        }
    }

    private lateinit var viewModel: TourOverviewViewModel
    private var _parkingETAAdapter: ParkingETAAdapter? = null
    private var _binding: FragmentTourOverviewBinding? = null

    /**
     * This property is only valid between onCreateView and
     * onDestroyView.
     */
    private val binding get() = _binding!!

    /**
     * This property is only valid between onCreateView and
     * onDestroyView.
     */
    private val parkingETAAdapter get() = _parkingETAAdapter!!


    override fun onCreate(savedInstanceState: Bundle?) {

        val routeId: Long = arguments!!.getLong(ARG_ROUTE_ID)
        super.onCreate(savedInstanceState)
        this.viewModel = ViewModelProvider(
            this,
            TourOverviewViewModel.TourOverviewViewModelFactory(
                requireActivity().application,
                routeId,
                null
            )
        )[TourOverviewViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTourOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupObserver()
        val location = Location("TEST")

        location.longitude = 11.2675967
        location.latitude = 49.1451127

        location.time = System.currentTimeMillis()
        viewModel.updateRouteETA(location)
    }

    private fun setupObserver() {
        viewModel.currentRouteETA.observe(viewLifecycleOwner) { routeETA ->
            routeETA?.let {
                parkingETAAdapter.submitList(routeETA.parkingETAs)
                // TODO add normal routeETA
            }
        }
    }

    private fun setupRecycler() {
        this._parkingETAAdapter = ParkingETAAdapter()
        this.binding.parkingRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        this.binding.parkingRecycler.adapter = this.parkingETAAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}