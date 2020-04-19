package de.timbolender.fefereader.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.util.PreferenceHelper

/**
 * Broadcast receiver starting the update service. Intended for automatic launching of service after boot.
 */
class BootServiceStarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action!!
        if (action != "android.intent.action.BOOT_COMPLETED" && action != "android.intent.action.MY_PACKAGE_REPLACED")
            return

        val repository = DataRepository(context)
        val notificationCreator = NotificationReceiver(repository)
        notificationCreator.register(context)

        val preferenceHelper = PreferenceHelper(context)
        UpdateWorker.configureAutomaticUpdates(context, preferenceHelper)
    }
}
