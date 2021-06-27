package de.timbolender.fefereader

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import com.facebook.stetho.Stetho

/**
 * Basic set up.
 */
class FefeReaderApplication : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration() =
            Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.DEBUG)
                    .build()

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}
