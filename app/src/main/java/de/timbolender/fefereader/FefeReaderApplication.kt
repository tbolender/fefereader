package de.timbolender.fefereader

import android.app.Application
import com.facebook.stetho.Stetho

/**
 * Basic set up.
 */
class FefeReaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }
}
