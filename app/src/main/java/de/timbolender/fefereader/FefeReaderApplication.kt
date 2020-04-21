package de.timbolender.fefereader

import android.app.Application
import android.content.Intent
import com.facebook.stetho.Stetho
import de.timbolender.fefereader.service.NotificationService

/**
 * Basic set up.
 */
class FefeReaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

        val serviceIntent = Intent(this, NotificationService::class.java)
        startService(serviceIntent)
    }
}
