package de.timbolender.fefereader.ui.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import de.timbolender.fefereader.R
import de.timbolender.fefereader.background.UpdateWorker.Companion.configureAutomaticUpdates
import de.timbolender.fefereader.util.PreferenceHelper

/**
 * Displays all settings of the app.
 */
class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    companion object {
        val TAG = SettingsFragment::class.simpleName!!
    }

    var automaticUpdatesKey: String? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load preferences from file
        addPreferencesFromResource(R.xml.preferences)

        // Register for changes
        automaticUpdatesKey = getString(R.string.pref_updates_enabled_key)
        val preference : Preference? = findPreference(automaticUpdatesKey!!)
        preference?.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val key = preference.key
        if (automaticUpdatesKey == key) {
            onAutomaticUpdatesToggle()
        } else {
            throw RuntimeException("Unknown preference change event received!")
        }
        return true
    }

    private fun onAutomaticUpdatesToggle() {
        val preferenceHelper = PreferenceHelper(activity)
        configureAutomaticUpdates(requireActivity(), preferenceHelper)
    }
}
