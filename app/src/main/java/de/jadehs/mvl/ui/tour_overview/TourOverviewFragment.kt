package de.jadehs.mvl.ui.tour_overview

import android.app.PendingIntent
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import de.jadehs.mvl.R
import de.jadehs.mvl.data.models.Coordinate
import de.jadehs.mvl.data.models.parking.Parking
import de.jadehs.mvl.data.models.parking.ParkingOccupancyReport
import de.jadehs.mvl.databinding.FragmentTourOverviewBinding
import de.jadehs.mvl.provider.ReportsFileProvider
import de.jadehs.mvl.reciever.ReportSharedReceiver
import de.jadehs.mvl.ui.dialog.ParkingReportDialog
import de.jadehs.mvl.ui.tour_overview.recycler.ParkingETAAdapter
import de.jadehs.mvl.ui.tour_overview.recycler.ToStartSmoothScroller
import de.jadehs.mvl.ui.tour_overview.recycler.TourOverviewLayoutManger
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.lang.IllegalStateException

class TourOverviewFragment : Fragment() {

    companion object {
        const val ARG_ROUTE_ID = "de.jadehs.mvl.TourOverviewFragment.route_id"
        const val PARKING_REPORT_TAG = "parking_occupancy_report_dialog"

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


    private lateinit var broadcastManager: LocalBroadcastManager
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

    private lateinit var arrivalString: String
    private lateinit var drivingString: String
    private lateinit var notDrivingString: String

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
        arrivalString = context?.getString(R.string.arrival_time) ?: "%s %s"
        drivingString = getString(R.string.currently_driving)
        notDrivingString = getString(R.string.not_currently_driving)
        viewModel.startETAUpdates()
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
        setupETAToggle()

        setupParkingReports()
        setupMenu()
    }


    fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.send_report, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.report_send_item) {
                    sendReports()
                    return true
                }
                return false
            }

        }, viewLifecycleOwner)

    }


    // region setup

    private fun setupParkingReports() {
        childFragmentManager.setFragmentResultListener(
            ParkingReportDialog.REQUEST_CODE,
            viewLifecycleOwner
        ) { _, bundle ->
            val report =
                bundle.getParcelable<ParkingOccupancyReport>(ParkingReportDialog.RESULT_PARKING_OCCUPANCY_REPORT)
            report?.let {
                viewModel.addParkingReport(report)
            }
        }
    }

    private fun setupETAToggle() {

        binding.drivingStatusButton.setOnClickListener {
            viewModel.triggerDrivingStatus()
        }

        // LIVEDATA
        viewModel.preferences.currentlyDrivingLiveData.observe(viewLifecycleOwner) { currentlyDriving ->
            setDrivingStatus(currentlyDriving)
        }
    }

    private fun setupRoute() {
        binding.drivingStatusButton.visibility = View.INVISIBLE

        viewModel.currentRoute.observe(viewLifecycleOwner) { route ->
            binding.overviewParkingName.text = route.name
            parkingETAAdapter.route = route

        }
    }

    private fun setupRecycler() {

        // LAYOUT
        this.binding.parkingRecycler.layoutManager =
            TourOverviewLayoutManger(requireContext()).apply {
                setOnLayoutCompleted {
                    scrollTo?.let {
                        scrollToIfNeeded(it)
                    }
                }
            }


        // ADAPTER
        this._parkingETAAdapter = ParkingETAAdapter()
        this.parkingETAAdapter.setOnReportClickListener(this::showParkingReportDialog)
        this.binding.parkingRecycler.adapter = this.parkingETAAdapter
        this.parkingETAAdapter.setOnCurrentListChangedCallback { parkingETAs ->
            val nextParkingIndex = parkingETAs.indexOfFirst { it.eta != null }

            scrollToIfNeeded(nextParkingIndex)
        }


        // LIVEDATA Observer

        viewModel.currentRouteETA.observe(viewLifecycleOwner) { routeETA ->
            routeETA?.let {
                if (binding.parkingLoader.visibility != View.GONE) {
                    binding.parkingLoader.visibility = View.GONE
                }
                parkingETAAdapter.submitList(routeETA.parkingETAs)

                val arrivalTimeString = routeETA.destinationETA.etaWeather.run {
                    "$hourOfDay:$minuteOfHour"
                }

                binding.overviewDestinationTime.text = arrivalString.format(arrivalTimeString, "")
                binding.drivingStatusButton.visibility = View.VISIBLE
            }
        }

        viewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            parkingETAAdapter.currentLocation = Coordinate.fromLocation(location)
        }
    }

    // endregion setup

    private fun setDrivingStatus(isDriving: Boolean) {
        binding.drivingStatusButton.text = if (isDriving) drivingString else notDrivingString
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
        val recyclerSmoothScroller = ToStartSmoothScroller(requireContext())
        recyclerSmoothScroller.targetPosition = position
        binding.parkingRecycler.layoutManager?.startSmoothScroll(recyclerSmoothScroller)
    }


    private fun sendReports() {
        viewModel.makeReportsZipFile().observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { reportsFile ->
                    val reportsUri = FileProvider.getUriForFile(
                        requireActivity(),
                        ReportsFileProvider.AUTHORITY,
                        reportsFile
                    )

                    val emailIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_STREAM, reportsUri)
                        putExtra(
                            Intent.EXTRA_EMAIL,
                            arrayOf(requireContext().getString(R.string.report_email))
                        )
                        putExtra(Intent.EXTRA_SUBJECT, "Reports ")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Sehr geehrtes MeteoValueLive-Team,\n" +
                                    "im Anhang finden Sie die Parkplatz- und ETA-Berichte die bisher angefallen sind.\n" +
                                    "\n" +
                                    "Mit freundlichen Grüßen\n" +
                                    ""
                        )
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        clipData = ClipData.newRawUri("Report Data", reportsUri)

                        type = "application/zip"
                    }


                    val choosenReciever =
                        ReportSharedReceiver.newPendingIntent(requireContext(), viewModel.routeId)

                    startActivity(
                        Intent.createChooser(
                            emailIntent,
                            context?.getString(R.string.report_send),
                            choosenReciever.intentSender
                        )
                    )
                },
                onError = { error ->
                    Log.e("ZIPFILE", "sendReports: Fail while generating zip file", error)
                }
            )
    }

    private fun showParkingReportDialog(parking: Parking) {
        ParkingReportDialog.newInstance(parking).show(childFragmentManager, PARKING_REPORT_TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scrollTo = null
    }


}