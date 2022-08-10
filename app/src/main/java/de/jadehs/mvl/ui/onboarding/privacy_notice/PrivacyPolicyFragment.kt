package de.jadehs.mvl.ui.onboarding.privacy_notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import de.jadehs.mvl.R
import de.jadehs.mvl.databinding.FragmentPrivacyPolicyBinding
import de.jadehs.mvl.settings.MainSharedPreferences

/**
 * Fragment to display the privacy notice and forcing the user to accept it to proceed
 * create an instance of this fragment.
 */
class PrivacyPolicyFragment : Fragment() {

    private lateinit var preferences: MainSharedPreferences
    private var _binding: FragmentPrivacyPolicyBinding? = null

    /**
     * Only valid after onCreateView and before onDestroyView
     */
    private val binding: FragmentPrivacyPolicyBinding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.preferences = MainSharedPreferences(requireContext())
        setupAcceptButton()
        setupDeclineButton()
    }

    private fun setupAcceptButton() {
        binding.acceptButton.setOnClickListener {
            acceptPolicy()
        }
    }

    private fun setupDeclineButton() {
        binding.declineButton.setOnClickListener {
            declinePolicy()
        }
    }

    private fun acceptPolicy() {
        preferences.acceptedPrivacyPolicy = true
        findNavController().navigateUp()
    }

    private fun declinePolicy() {
        preferences.acceptedPrivacyPolicy = false
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        this.preferences.recycle()
    }
}