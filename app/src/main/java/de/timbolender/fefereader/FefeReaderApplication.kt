package de.timbolender.fefereader

import android.app.Application
import com.facebook.stetho.Stetho
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.service.NotificationReceiver

/**
 * Basic set up.
 */
class FefeReaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
        registerNotificationCreator()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    private fun registerNotificationCreator() {
        val repository = DataRepository(this)
        val notificationCreator = NotificationReceiver(repository)
        notificationCreator.register(this)
    }
}
