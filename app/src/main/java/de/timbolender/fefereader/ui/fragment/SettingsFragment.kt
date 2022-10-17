package de.timbolender.fefereader.ui.fragment

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import de.timbolender.fefereader.R
import de.timbolender.fefereader.background.UpdateWorker.Companion.configureAutomaticUpdates
import de.timbolender.fefereader.ui.fragment.SettingsFragment
import de.timbolender.fefereader.util.PreferenceHelper

/**
 * Displays all settings of the app.
 */
class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {
    var automaticUpdatesKey: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load preferences from file
        addPreferencesFromResource(R.xml.preferences)

        // Register for changes
        automaticUpdatesKey = getString(R.string.pref_updates_enabled_key)
        findPreference(automaticUpdatesKey).onPreferenceChangeListener = this
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
        configureAutomaticUpdates(activity, preferenceHelper)
    }

    companion object {
        val TAG = SettingsFragment::class.java.simpleName
    }
}
