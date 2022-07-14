package de.jadehs.mvl.ui.tour_overview

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import de.jadehs.mvl.R
import de.jadehs.mvl.databinding.FragmentTourOverviewBinding
import de.jadehs.mvl.ui.tour_overview.recycler.ParkingETAAdapter
import de.jadehs.mvl.ui.tour_overview.recycler.TourOverviewLayoutManger

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

    private val arrivalString = context?.getString(R.string.arrival_time) ?: "%s %s"

    private var scrollTo: Int? = null


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

        setupRoute()
        setupRecycler()
        val location = Location("TEST")

        location.longitude = 11.2675967
        location.latitude = 49.1451127

        location.time = System.currentTimeMillis()
        viewModel.updateRouteETA(location)
    }

    private fun setupRoute() {
        binding.drivingStatusButton.visibility = View.INVISIBLE

        viewModel.currentRoute.observe(viewLifecycleOwner) { route ->
            binding.overviewParkingName.text = route.name

        }
    }

    private fun setupRecycler() {
        this._parkingETAAdapter = ParkingETAAdapter()

        this.parkingETAAdapter.setOnCurrenListChangedCallback { parkingETAs ->
            val nextParkingIndex = parkingETAs.indexOfFirst { it.eta != null }

            scrollToIfNeeded(nextParkingIndex)
        }



        this.binding.parkingRecycler.layoutManager =
            TourOverviewLayoutManger(requireContext()).apply {
                setOnLayoutCompleted {
                    scrollTo?.let {
                        scrollToIfNeeded(it)
                    }
                }
            }



        this.binding.parkingRecycler.adapter = this.parkingETAAdapter

        viewModel.currentRouteETA.observe(viewLifecycleOwner) { routeETA ->
            routeETA?.let {
                if (routeETA.parkingETAs.size > 0 && binding.parkingLoader.visibility != View.GONE) {
                    binding.parkingLoader.visibility = View.GONE
                }
                parkingETAAdapter.submitList(routeETA.parkingETAs)

                val arrivalTimeString = routeETA.destinationETA.etaWeather.run {
                    "$hourOfDay:$minuteOfHour"
                }

                binding.overviewDestinationTime.text = arrivalString.format(arrivalTimeString, "")
                binding.drivingStatusButton.visibility = View.VISIBLE

                // TODO add normal routeETA
            }
        }
    }

    private fun scrollToIfNeeded(position: Int) {
        this.scrollTo = null
        val vectorProvider =
            (binding.parkingRecycler.layoutManager as RecyclerView.SmoothScroller.ScrollVectorProvider)
        val vector = vectorProvider.computeScrollVectorForPosition(position)

        if (vector == null) {
            scrollTo = position
            return
        }


        if (vector.y >= 0) {
            scrollToItem(position)
        }
    }


    private fun scrollToItem(position: Int) {
        val recyclerSmoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        recyclerSmoothScroller.targetPosition = position
        binding.parkingRecycler.layoutManager?.startSmoothScroll(recyclerSmoothScroller)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scrollTo = null
    }


}