package de.timbolender.fefereader.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;

import androidx.annotation.Nullable;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.db.DataRepository;
import de.timbolender.fefereader.network.Updater;
import de.timbolender.fefereader.util.PreferenceHelper;

/**
 * Perform regular post updates in the background.
 * TODO: Migrate to WorkManager as soon as its fully AndroidX
 */
public class UpdateService extends Service {
    static final String TAG = UpdateService.class.getSimpleName();
    static final String ACTION_UPDATE = "de.timbolender.fefereader.service.action.UPDATE";
    static final String ACTION_ENABLE_REGULAR_UPDATES = "de.timbolender.fefereader.service.action.ENABLE_UPDATES";
    static final String ACTION_DISABLE_REGULAR_UPDATES = "de.timbolender.fefereader.service.action.DISABLE_UPDATES";

    public static final String BROADCAST_UPDATE_SKIPPED = "de.timbolender.fefereader.service.action.UPDATE_SKIPPED";
    public static final String BROADCAST_UPDATE_FINISHED = "de.timbolender.fefereader.service.action.UPDATE_FINISHED";
    public static final String EXTRA_UPDATE_SUCCESS = "update_success";
    public static final int BROADCAST_PRIORITY_UI = 10;
    public static final int BROADCAST_PRIORITY_SERVICE = 0;
    private static final String CHANNEL_ID = "default";

    /**
     * @param context  Context to use.
     * @return Intent with which to start the update routine.
     */
    private static Intent createUpdateIntent(Context context) {
        Intent updateIntent = new Intent(context, UpdateService.class);
        updateIntent.setAction(ACTION_UPDATE);
        return updateIntent;
    }

    /**
     * Trigger update in background service.
     * @param context Context to use.
     */
    public static void startManualUpdate(Context context) {
        Intent updateIntent = createUpdateIntent(context);
        context.startService(updateIntent);
    }

    /**
     * Start automatic updates in background service.
     * @param context Context to use.
     */
    public static void enableAutomaticUpdates(Context context) {
        Intent enableIntent = new Intent(context, UpdateService.class);
        enableIntent.setAction(ACTION_ENABLE_REGULAR_UPDATES);
        context.startService(enableIntent);
    }

    /**
     * Stop automatic updates in background service.
     * @param context Context to use.
     */
    public static void disableAutomaticUpdates(Context context) {
        Intent disableIntent = new Intent(context, UpdateService.class);
        disableIntent.setAction(ACTION_DISABLE_REGULAR_UPDATES);
        context.startService(disableIntent);
    }

    PreferenceHelper preferenceHelper;
    BroadcastReceiver updateReceiver;
    Thread updateThread;

    DataRepository repository;

    @Override
    public void onCreate() {
        preferenceHelper = new PreferenceHelper(this);
        repository = new DataRepository(getApplication());
        updateThread = null;

        // Register broadcast receiver for notifications
        updateReceiver = new NotificationReceiver(repository, EXTRA_UPDATE_SUCCESS, CHANNEL_ID);
        IntentFilter intentFilter = new IntentFilter(BROADCAST_UPDATE_FINISHED);
        intentFilter.setPriority(BROADCAST_PRIORITY_SERVICE);
        registerReceiver(updateReceiver, intentFilter);

        // Register normal regular update behavior
        registerRegularUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        if(intent != null) {
            String action = intent.getAction();
            // Trigger update
            if (action.equals(ACTION_UPDATE)) {
                Log.i(TAG, "Starting update");
                performUpdate();
            }
            // Take care about background updates
            if (action.equals(ACTION_ENABLE_REGULAR_UPDATES)) {
                registerRegularUpdates();
            }
            if (action.equals(ACTION_DISABLE_REGULAR_UPDATES)) {
                unregisterRegularUpdates();
            }
        }

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

    /**
     * Start update in a separate thread (if not already running).
     */
    private void performUpdate() {
        // Check whether update is running
        if(updateThread != null) {
            Log.e(TAG, "Ignoring update request due to running update");
            Intent skippedIntent = new Intent(BROADCAST_UPDATE_SKIPPED);
            sendOrderedBroadcast(skippedIntent, null);
            return;
        }

        updateThread = new Thread(() -> {
            boolean success = false;

            try {
                new Updater(repository).update();
                success = true;
                Log.d(TAG, "Update finished");
            }
            catch(ParseException | IOException e) {
                e.printStackTrace();
            }
            finally {
                Intent finishedIntent = new Intent(BROADCAST_UPDATE_FINISHED);
                finishedIntent.putExtra(EXTRA_UPDATE_SUCCESS, success);
                sendOrderedBroadcast(finishedIntent, null);

                updateThread = null;
            }
        });
        updateThread.start();
    }

    /**
     * Set alarm to regularly check for new updates.
     */
    private void registerRegularUpdates() {
        boolean updatesEnabled = preferenceHelper.isUpdatesEnabled();
        int updateInterval = preferenceHelper.getUpdateInterval();

        if(updatesEnabled) {
            Log.d(TAG, "Requesting alarm for regular updates with period of " + updateInterval + "ms");
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent updateIntent = createUpdateIntent(this);
            // Double registration seemed to be prevented by AlarmManager (tests on 4.4 and 6.0.1)
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, updateIntent, 0);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + updateInterval, updateInterval, pendingIntent);
        }
        else {
            Log.d(TAG, "No automatic updates desired");
        }
    }

    /**
     * Cancel receiving of update alarm.
     */
    private void unregisterRegularUpdates() {
        Log.d(TAG, "Canceling any alarm for regular updates");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent updateIntent = createUpdateIntent(this);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, updateIntent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
