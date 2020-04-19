package de.timbolender.fefereader.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.timbolender.fefereader.R;
import de.timbolender.fefereader.service.UpdateWorker;
import de.timbolender.fefereader.util.PreferenceHelper;

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
            onAutomaticUpdatesToggle();
        }
        else {
            throw new RuntimeException("Unknown preference change event received!");
        }

        return true;
    }

    private void onAutomaticUpdatesToggle() {
        PreferenceHelper preferenceHelper = new PreferenceHelper(getActivity());
        UpdateWorker.Companion.configureAutomaticUpdates(getActivity(), preferenceHelper);
    }
}
