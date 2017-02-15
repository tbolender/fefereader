package de.timbolender.fefereader.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.timbolender.fefereader.R;

/**
 * Displays all settings of the app.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load preferences from file
        addPreferencesFromResource(R.xml.preferences);
    }
}