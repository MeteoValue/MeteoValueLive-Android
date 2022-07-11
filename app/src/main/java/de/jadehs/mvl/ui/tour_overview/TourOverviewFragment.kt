package de.jadehs.mvl.ui.tour_overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.jadehs.mvl.R
import de.jadehs.mvl.databinding.FragmentTourOverviewBinding

class TourOverviewFragment : Fragment() {

    private var _binding: FragmentTourOverviewBinding? = null

    /**
     * This property is only valid between onCreateView and
     * onDestroyView.
     */
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}