package de.timbolender.fefereader.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import de.timbolender.fefereader.R;
import de.timbolender.fefereader.service.UpdateService;

/**
 * Displays all settings of the app.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    static final String TAG = SettingsFragment.class.getSimpleName();

    String automaticUpdatesKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load preferences from file
        addPreferencesFromResource(R.xml.preferences);

        // Register for changes
        automaticUpdatesKey = getString(R.string.pref_updates_enabled_key);
        findPreference(automaticUpdatesKey).setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if(automaticUpdatesKey.equals(key)) {
            onAutomaticUpdatesToggle((Boolean) newValue);
        }
        else {
            throw new RuntimeException("Unknown preference change event received!");
        }

        return true;
    }

    private void onAutomaticUpdatesToggle(boolean isActivated) {
        if(isActivated) {
            Log.d(TAG, "Enabling automatic updates");
            UpdateService.enableAutomaticUpdates(getActivity());
        }
        else {
            Log.d(TAG, "Disabling automatic updates");
            UpdateService.disableAutomaticUpdates(getActivity());
        }
    }
}