package de.timbolender.fefereader.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.timbolender.fefereader.util.PreferenceHelper

/**
 * Broadcast receiver starting the notification service. Sets up automatic updates as well.
 */
class BootServiceStarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action!!
        if (action != "android.intent.action.BOOT_COMPLETED" && action != "android.intent.action.MY_PACKAGE_REPLACED")
            return

        val appContext = context.applicationContext
        val preferenceHelper = PreferenceHelper(appContext)
        UpdateWorker.configureAutomaticUpdates(appContext, preferenceHelper)
    }
}
