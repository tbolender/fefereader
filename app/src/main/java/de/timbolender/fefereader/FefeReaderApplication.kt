package de.timbolender.fefereader

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho

/**
 * Basic set up.
 */
class FefeReaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}
