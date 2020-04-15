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
import android.util.Log;

import androidx.annotation.Nullable;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.db.DataRepository;

import static de.timbolender.fefereader.service.UpdateWorker.BROADCAST_PRIORITY_SERVICE;
import static de.timbolender.fefereader.service.UpdateWorker.BROADCAST_UPDATE_FINISHED;
import static de.timbolender.fefereader.service.UpdateWorker.EXTRA_UPDATE_SUCCESS;

/**
 * Perform regular post updates in the background.
 */
public class UpdateService extends Service {
    static final String TAG = UpdateService.class.getSimpleName();

    private static final String CHANNEL_ID = "default";

    BroadcastReceiver updateReceiver;
    DataRepository repository;

    @Override
    public void onCreate() {
        repository = new DataRepository(getApplication());

        // Register broadcast receiver for notifications
        Log.i(TAG, "Register background broadcast receiver");
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
        Log.i(TAG, "Unregister background broadcast receiver");
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
