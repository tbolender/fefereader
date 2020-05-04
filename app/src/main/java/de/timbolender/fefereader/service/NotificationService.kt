package de.timbolender.fefereader.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.util.PreferenceHelper

class NotificationService : Service() {
    companion object {
        fun startService(context: Context) {
            val serviceIntent = Intent(context.applicationContext, NotificationService::class.java)
            context.applicationContext.startService(serviceIntent)
        }
    }

    lateinit var notificationCreator: NotificationReceiver

    override fun onCreate() {
        super.onCreate()

        val repository = DataRepository(this)
        notificationCreator = NotificationReceiver(repository)
        notificationCreator.register(this)

        val preferenceHelper = PreferenceHelper(this)
        UpdateWorker.configureAutomaticUpdates(this, preferenceHelper)
    }

    override fun onDestroy() {
        notificationCreator.unregister(this)

        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY;
    }

    override fun onBind(intent: Intent): IBinder? {
        // We do not support binding
        return null
    }
}
