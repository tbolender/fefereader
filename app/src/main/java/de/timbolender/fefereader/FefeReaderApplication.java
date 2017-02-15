package de.timbolender.fefereader;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Basic set up.
 */
public class FefeReaderApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
    }
}
