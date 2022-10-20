package de.timbolender.fefereader.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import androidx.preference.PreferenceManager;
import de.timbolender.fefereader.R;

/**
 * Class to simplify access to default settings with resource ids.
 */
public class PreferenceHelper {
    private static final Map<String, Integer> AVAILABLE_STYLES = Collections.unmodifiableMap(new HashMap<String, Integer>() {{
        put("post_style_none", R.string.post_style_base);
        put("post_style_default", R.string.post_style_default);
    }});

    private final String PREF_UPDATES_ENABLED;
    private final boolean PREF_UPDATES_ENABLED_DEFAULT;

    private final String PREF_UPDATE_INTERVAL;
    private final String PREF_UPDATE_INTERVAL_DEFAULT;

    private final String PREF_INSPECT_URL;
    private final boolean PREF_INSPECT_URL_DEFAULT;

    private final String PREF_POST_STYLE;
    private final String PREF_POST_STYLE_DEFAULT;

    private final Resources resources;
    private final SharedPreferences pref;

    /**
     * Initialize object with given values.
     *
     * @param context Context of default settings to retrieve key and default value from.
     */
    public PreferenceHelper(Context context) {
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.resources = context.getResources();

        PREF_UPDATES_ENABLED = resources.getString(R.string.pref_updates_enabled_key);
        PREF_UPDATES_ENABLED_DEFAULT = resources.getBoolean(R.bool.pref_updates_enabled_default);
        PREF_UPDATE_INTERVAL = resources.getString(R.string.pref_update_interval_key);
        PREF_UPDATE_INTERVAL_DEFAULT = resources.getString(R.string.pref_update_interval_default);
        PREF_INSPECT_URL = resources.getString(R.string.pref_inspect_url_key);
        PREF_INSPECT_URL_DEFAULT = resources.getBoolean(R.bool.pref_inspect_url_default);
        PREF_POST_STYLE = resources.getString(R.string.pref_post_style_key);
        PREF_POST_STYLE_DEFAULT = resources.getString(R.string.pref_post_style_default);
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

    public long getUpdateInterval() {
        return Long.parseLong(pref.getString(PREF_UPDATE_INTERVAL, PREF_UPDATE_INTERVAL_DEFAULT));
    }

    public void setUpdateInterval(int updateInterval) {
        pref.edit().putInt(PREF_UPDATE_INTERVAL, updateInterval).apply();
    }

    public boolean isUrlInspectionEnabled() {
        return pref.getBoolean(PREF_INSPECT_URL, PREF_INSPECT_URL_DEFAULT);
    }

    public void setUrlInspectionEnabled(boolean inspectionEnabled) {
        pref.edit().putBoolean(PREF_INSPECT_URL, inspectionEnabled).apply();
    }

    public String getPostStyle() {
        String chosenStyle = pref.getString(PREF_POST_STYLE, PREF_POST_STYLE_DEFAULT);
        String styleContent = "";
        if(!chosenStyle.equals("post_style_none")) {
            styleContent = resources.getString(AVAILABLE_STYLES.get(chosenStyle));
        }
        return getBaseStyle() + styleContent;
    }

    private String getBaseStyle() {
        // Construct none style
        int defaultMargin = resources.getDimensionPixelSize(R.dimen.post_view_margin);
        // TODO: Get proper color of the current theme
        // TODO: Make background of quoting for default style
        //noinspection ResourceType
        String textColor = resources.getString(android.R.color.secondary_text_light).replace("#ff", "#");
        //noinspection ResourceType
        String linkColor = resources.getString(R.color.colorAccent).replace("#ff", "#");
        return resources.getString(R.string.post_style_base, defaultMargin, textColor, linkColor);
    }
}
