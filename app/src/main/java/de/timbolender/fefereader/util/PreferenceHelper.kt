package de.timbolender.fefereader.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager
import de.timbolender.fefereader.R
import java.util.*

/**
 * Class to simplify access to default settings with resource ids.
 */
class PreferenceHelper(context: Context) {
    companion object {
        private val AVAILABLE_STYLES = Collections.unmodifiableMap(object : HashMap<String?, Int?>() {
            init {
                put("post_style_none", R.string.post_style_base)
                put("post_style_default", R.string.post_style_default)
            }
        })
    }

    private val PREF_UPDATES_ENABLED: String
    private val PREF_UPDATES_ENABLED_DEFAULT: Boolean
    private val PREF_UPDATE_INTERVAL: String
    private val PREF_UPDATE_INTERVAL_DEFAULT: String
    private val PREF_INSPECT_URL: String
    private val PREF_INSPECT_URL_DEFAULT: Boolean
    private val PREF_POST_STYLE: String
    private val PREF_POST_STYLE_DEFAULT: String
    private val resources: Resources
    val sharedPreferences: SharedPreferences

    /**
     * Initialize object with given values.
     *
     * @param context Context of default settings to retrieve key and default value from.
     */
    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        resources = context.resources
        PREF_UPDATES_ENABLED = resources.getString(R.string.pref_updates_enabled_key)
        PREF_UPDATES_ENABLED_DEFAULT = resources.getBoolean(R.bool.pref_updates_enabled_default)
        PREF_UPDATE_INTERVAL = resources.getString(R.string.pref_update_interval_key)
        PREF_UPDATE_INTERVAL_DEFAULT = resources.getString(R.string.pref_update_interval_default)
        PREF_INSPECT_URL = resources.getString(R.string.pref_inspect_url_key)
        PREF_INSPECT_URL_DEFAULT = resources.getBoolean(R.bool.pref_inspect_url_default)
        PREF_POST_STYLE = resources.getString(R.string.pref_post_style_key)
        PREF_POST_STYLE_DEFAULT = resources.getString(R.string.pref_post_style_default)
    }

    var isUpdatesEnabled: Boolean
        get() = sharedPreferences.getBoolean(PREF_UPDATES_ENABLED, PREF_UPDATES_ENABLED_DEFAULT)
        set(updatesEnabled) {
            sharedPreferences.edit().putBoolean(PREF_UPDATES_ENABLED, updatesEnabled).apply()
        }

    var updateInterval: Int
        get() = sharedPreferences.getString(PREF_UPDATE_INTERVAL, PREF_UPDATE_INTERVAL_DEFAULT)!!.toInt()
        set(updateInterval) = sharedPreferences.edit().putInt(PREF_UPDATE_INTERVAL, updateInterval).apply()

    var isUrlInspectionEnabled: Boolean
        get() = sharedPreferences.getBoolean(PREF_INSPECT_URL, PREF_INSPECT_URL_DEFAULT)
        set(inspectionEnabled) {
            sharedPreferences.edit().putBoolean(PREF_INSPECT_URL, inspectionEnabled).apply()
        }

    val postStyle: String
        get() {
            val chosenStyle = sharedPreferences.getString(PREF_POST_STYLE, PREF_POST_STYLE_DEFAULT)
            var styleContent = ""
            if (chosenStyle != "post_style_none") {
                styleContent = resources.getString(AVAILABLE_STYLES[chosenStyle]!!)
            }
            return baseStyle + styleContent
        }

    // Construct none style
    private val baseStyle: String
        @SuppressLint("ResourceType")
        get() {
            // TODO: Get proper color of the current theme
            // TODO: Make background of quoting for default style
            // Construct none style
            val defaultMargin = resources.getDimensionPixelSize(R.dimen.post_view_margin)
            val textColor = resources.getString(android.R.color.secondary_text_light).replace("#ff", "#")
            val linkColor = resources.getString(R.color.colorAccent).replace("#ff", "#")
            return resources.getString(R.string.post_style_base, defaultMargin, textColor, linkColor)
        }
}
