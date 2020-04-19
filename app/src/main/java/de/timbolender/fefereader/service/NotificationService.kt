package de.timbolender.fefereader.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import de.timbolender.fefereader.db.DataRepository

class NotificationService : Service() {
    lateinit var notificationCreator: NotificationReceiver

    override fun onCreate() {
        super.onCreate()

        // Register long-running
        val repository = DataRepository(this)
        notificationCreator = NotificationReceiver(repository)
        notificationCreator.register(this)
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
