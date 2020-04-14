package de.timbolender.fefereader.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.db.DataRepository;

/**
 * Perform regular post updates in the background.
 * TODO: Migrate to WorkManager as soon as its fully AndroidX
 */
public class UpdateService extends Service {
    static final String TAG = UpdateService.class.getSimpleName();

    public static final String BROADCAST_UPDATE_SKIPPED = "de.timbolender.fefereader.service.action.UPDATE_SKIPPED";
    public static final String BROADCAST_UPDATE_FINISHED = "de.timbolender.fefereader.service.action.UPDATE_FINISHED";
    public static final String EXTRA_UPDATE_SUCCESS = "update_success";
    public static final int BROADCAST_PRIORITY_UI = 10;
    public static final int BROADCAST_PRIORITY_SERVICE = 0;
    private static final String CHANNEL_ID = "default";

    BroadcastReceiver updateReceiver;
    DataRepository repository;

    @Override
    public void onCreate() {
        repository = new DataRepository(getApplication());

        // Register broadcast receiver for notifications
        updateReceiver = new NotificationReceiver(repository, EXTRA_UPDATE_SUCCESS, CHANNEL_ID);
        IntentFilter intentFilter = new IntentFilter(BROADCAST_UPDATE_FINISHED);
        intentFilter.setPriority(BROADCAST_PRIORITY_SERVICE);
        registerReceiver(updateReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(updateReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // We do not support binding
        return null;
    }

    /**
     * Create notification channel for Android O.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.notification_channel_name);
            String descriptionText = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(descriptionText);
            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

    }
}
