package de.timbolender.fefereader.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import de.timbolender.fefereader.R;

/**
 * Class to simplify access to default settings with resource ids.
 */
public class PreferenceHelper {
    private final String PREF_UPDATES_ENABLED;
    private final boolean PREF_UPDATES_ENABLED_DEFAULT;
    private final String PREF_UPDATE_INTERVAL;
    private final String PREF_UPDATE_INTERVAL_DEFAULT;

    private final SharedPreferences pref;

    /**
     * Initialize object with given values.
     *
     * @param context Context of default settings to retrieve key and default value from.
     */
    public PreferenceHelper(Context context) {
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);

        Resources resources = context.getResources();
        PREF_UPDATES_ENABLED = resources.getString(R.string.pref_updates_enabled_key);
        PREF_UPDATES_ENABLED_DEFAULT = resources.getBoolean(R.bool.pref_updates_enabled_default);
        PREF_UPDATE_INTERVAL = resources.getString(R.string.pref_update_interval_key);
        PREF_UPDATE_INTERVAL_DEFAULT = resources.getString(R.string.pref_update_interval_default);
    }

    public SharedPreferences getSharedPreferences() {
        return pref;
    }

    public boolean isUpdatesEnabled() {
        return pref.getBoolean(PREF_UPDATES_ENABLED, PREF_UPDATES_ENABLED_DEFAULT);
    }

    public void setUpdatesEnabled(boolean updatesEnabled) {
        pref.edit().putBoolean(PREF_UPDATES_ENABLED, updatesEnabled).apply();
    }

    public int getUpdateInterval() {
        return Integer.parseInt(pref.getString(PREF_UPDATE_INTERVAL, PREF_UPDATE_INTERVAL_DEFAULT));
    }

    public void setUpdateInterval(int updateInterval) {
        pref.edit().putInt(PREF_UPDATE_INTERVAL, updateInterval).apply();
    }
}
