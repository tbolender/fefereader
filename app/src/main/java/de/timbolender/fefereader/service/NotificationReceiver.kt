package de.timbolender.fefereader.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import de.timbolender.fefereader.db.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Catches the successful update broadcast and creates a matching notification.
 */
class NotificationReceiver(val repository: DataRepository,
                           val updateSuccessExtra: String,
                           val channelId: String): BroadcastReceiver() {
    companion object {
        val TAG: String = NotificationReceiver::class.simpleName!!
        const val NOTIFICATION_ID = 42 // What else?
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        CoroutineScope(Dispatchers.Main).launch {
            if(context == null || intent == null) return@launch
            Log.d(TAG, "Received update broadcast, no ui visible currently")

            // Only show notification if there is something to report
            val success = intent.getBooleanExtra(updateSuccessExtra, false)
            val builder = NotificationBuilder(context, repository)

            if (success && builder.isNotificationNecessary()) {
                Log.d(TAG, "Showing notification")
                val notification = builder.createNotification(channelId)
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)
            }
        }
    }
}
