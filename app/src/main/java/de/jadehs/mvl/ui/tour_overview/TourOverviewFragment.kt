package de.jadehs.mvl.ui.tour_overview

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
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
import de.jadehs.mvl.ui.dialog.PeriodDialog
import de.jadehs.mvl.ui.dialog.ResetDrivingTimeDialog
import de.jadehs.mvl.ui.tour_overview.recycler.ParkingETAAdapter
import de.jadehs.mvl.ui.tour_overview.recycler.ToStartSmoothScroller
import de.jadehs.mvl.ui.tour_overview.recycler.TourOverviewLayoutManger
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Period
import org.joda.time.ReadableInstant
import org.joda.time.format.PeriodFormatterBuilder
import java.io.File

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
            return bundleOf(ARG_ROUTE_ID to routeId)
        }
    }


    private lateinit var drivingTimeLimitPrefix: String
    private lateinit var drivingTimeLimitSuffix: String
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
        arrivalString = getString(R.string.arrival_time)
        drivingString = getString(R.string.currently_driving)
        notDrivingString = getString(R.string.not_currently_driving)
        drivingTimeLimitPrefix = getString(R.string.driving_time_prefix)
        drivingTimeLimitSuffix = getString(R.string.driving_time_suffix)
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
        childFragmentManager.setFragmentResultListener(
            PeriodDialog.REQUEST_CODE,
            viewLifecycleOwner
        ) { _, result ->
            val period = result.getLong(PeriodDialog.RESULT_DRIVING_TIME)

            period.takeUnless { it <= 0 }?.let {
                onNewDrivingLimit(period)
            }
        }

        setupRoute()
        setupRecycler()
        setupETAToggle()
        setupDrivingTime()

        setupParkingReports()
        setupMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scrollTo = null
    }

    // region setup
    private fun setupDrivingTime() {


        binding.overviewDrivingTimeEdit.setOnClickListener {
            openDrivingLimitDialog()
        }

        binding.overviewDrivingTime.periodFormatter =
            PeriodFormatterBuilder().printZeroIfSupported()
                .appendLiteral("$drivingTimeLimitPrefix ")
                .minimumPrintedDigits(1).appendHours().appendLiteral(":").minimumPrintedDigits(2)
                .appendMinutes().appendLiteral(" $drivingTimeLimitSuffix").toFormatter()

        binding.overviewDrivingTime.visibility = View.INVISIBLE

        viewModel.preferences.currentDrivingLimitLiveData.observe(viewLifecycleOwner) { drivingLimit ->
            drivingLimit?.let {
                binding.overviewDrivingTime.countDownDestination = drivingLimit.toInstant()
                binding.overviewDrivingTime.visibility = View.VISIBLE
            } ?: kotlin.run {
                binding.overviewDrivingTime.visibility = View.INVISIBLE
            }
        }
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


    private fun setupParkingReports() {
        childFragmentManager.setFragmentResultListener(
            ParkingReportDialog.REQUEST_CODE,
            viewLifecycleOwner
        ) { _, bundle ->
            val report =
                bundle.getParcelable<ParkingOccupancyReport>(ParkingReportDialog.RESULT_PARKING_OCCUPANCY_REPORT)
            report?.let {
                Toast.makeText(requireContext(), R.string.report_thanks, Toast.LENGTH_LONG).show()
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

            if (currentlyDriving) {
                if (viewModel.shouldUpdateDrivingTime()) {
                    ResetDrivingTimeDialog.newInstance().show(childFragmentManager, null)
                }
            }
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

                val arrivalTime = routeETA.destinationETA.etaWeather

                binding.overviewDestinationTime.text =
                    arrivalString.format(arrivalTime.hourOfDay, arrivalTime.minuteOfHour, "")
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
                    val emailIntent = getEmailIntent(reportsFile)


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

    private fun getEmailIntent(reportsFile: File): Intent {
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
        return emailIntent
    }

    private fun showParkingReportDialog(parking: Parking) {
        ParkingReportDialog.newInstance(parking).show(childFragmentManager, PARKING_REPORT_TAG)
    }

    private fun openDrivingLimitDialog() {
        @Suppress("CAST_NEVER_SUCCEEDS")
        PeriodDialog.newInstance(
            getString(R.string.driving_time_dialog_title),
            getString(R.string.driving_time_dialog_description),
            Period(
                null as? ReadableInstant,
                viewModel.preferences.currentDrivingLimit
            ).takeUnless { it.toStandardDuration().millis < 0 }
        ).show(childFragmentManager, null)
    }

    private fun onNewDrivingLimit(period: Long) {
        viewModel.preferences.currentDrivingLimit = DateTime.now().plus(period)
    }


}