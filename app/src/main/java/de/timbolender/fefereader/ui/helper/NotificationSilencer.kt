package de.timbolender.fefereader.ui.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContextCompat
import de.timbolender.fefereader.background.UpdateWorker

class NotificationSilencer : BroadcastReceiver() {
    companion object {
        val TAG: String = NotificationSilencer::class.simpleName!!
    }

    /**
     * Register broadcast receiver for update notifications.
     * @param context Context to register with
     */
    fun register(context: Context) {
        val intentFilter = IntentFilter(UpdateWorker.BROADCAST_UPDATE_FINISHED)
        intentFilter.priority = UpdateWorker.BROADCAST_PRIORITY_UI
        ContextCompat.registerReceiver(context.applicationContext, this, intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    /**
     * Unregister broadcast receiver.
     */
    fun unregister(context: Context) {
        context.applicationContext.unregisterReceiver(this)
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d(TAG, "Received content update notification")
        abortBroadcast()
    }
}
