package de.timbolender.fefereader.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver starting the update service. Intended for automatic launching of service after boot.
 */
class BootServiceStarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action!!
        if (action != "android.intent.action.BOOT_COMPLETED" && action != "android.intent.action.MY_PACKAGE_REPLACED")
            return

        UpdateService.startManualUpdate(context)
    }
}
