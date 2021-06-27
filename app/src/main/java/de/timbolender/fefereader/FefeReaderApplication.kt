package de.timbolender.fefereader

import android.app.Application
import androidx.work.Configuration
import com.facebook.stetho.Stetho

/**
 * Basic set up.
 */
class FefeReaderApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return if (BuildConfig.DEBUG) {
            Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.DEBUG)
                    .build()
        } else {
            Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.INFO)
                    .build()
        }
    }
}
