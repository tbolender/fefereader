package de.timbolender.fefereader.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import de.timbolender.fefereader.R
import de.timbolender.fefereader.db.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Catches the successful update broadcast and creates a matching notification.
 */
class NotificationReceiver(val updateSuccessExtra: String = UpdateWorker.EXTRA_UPDATE_SUCCESS,
                           val channelId: String = CHANNEL_ID): BroadcastReceiver() {
    companion object {
        val TAG: String = NotificationReceiver::class.simpleName!!
        const val NOTIFICATION_ID = 42 // What else?

        const val CHANNEL_ID = "default"

        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(TAG, "Create notification channel")
                val name: String = context.getString(R.string.notification_channel_name)
                val descriptionText: String = context.getString(R.string.notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance)
                channel.description = descriptionText
                // Register the channel with the system
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    /**
     * Register broadcast receiver for notification creation.
     * @param context Context to register with
     */
    fun register(context: Context) {
        Log.i(TAG, "Register background broadcast receiver")
        val intentFilter = IntentFilter(UpdateWorker.BROADCAST_UPDATE_FINISHED)
        intentFilter.priority = UpdateWorker.BROADCAST_PRIORITY_SERVICE
        context.applicationContext.registerReceiver(this, intentFilter)
    }

    /**
     * Unregister broadcast receiver.
     */
    fun unregister(context: Context) {
        context.applicationContext.unregisterReceiver(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        CoroutineScope(Dispatchers.Main).launch {
            if(context == null || intent == null) return@launch
            Log.d(TAG, "Received update broadcast, no ui visible currently")

            // Only show notification if there is something to report
            val success = intent.getBooleanExtra(updateSuccessExtra, false)
            val repository = DataRepository(context)
            val builder = NotificationBuilder(context, repository)

            if (success && builder.isNotificationNecessary()) {
                createNotificationChannel(context)

                Log.d(TAG, "Showing notification")
                val notification = builder.createNotification(channelId)
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)
            }
        }
    }
}
