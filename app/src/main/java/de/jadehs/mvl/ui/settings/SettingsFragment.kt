package de.jadehs.mvl.ui.settings

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import de.jadehs.mvl.R
import de.jadehs.mvl.settings.preferences.TimePreference
import de.jadehs.mvl.settings.preferences.TimePreferenceDialog


const val DIALOG_TAG: String = "de.jadehs.mvl.DIALOG"


class SettingsFragment : PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onPreferenceDisplayDialog(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        var handled = false
        var f: DialogFragment? = null
        if (pref is TimePreference) {
            f = TimePreferenceDialog.newInstance(pref.key)
            handled = true
        }

        f?.setTargetFragment(caller, 0)
        f?.show(parentFragmentManager, DIALOG_TAG)


        return handled
    }


}