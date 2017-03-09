package de.timbolender.fefereader.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.RawPost;
import de.timbolender.fefereader.db.DatabaseWrapper;
import de.timbolender.fefereader.db.SQLiteOpenHelper;
import de.timbolender.fefereader.db.SQLiteWrapper;
import de.timbolender.fefereader.network.Fetcher;
import de.timbolender.fefereader.network.Parser;
import de.timbolender.fefereader.ui.MainActivity;
import de.timbolender.fefereader.util.PreferenceHelper;
import okhttp3.OkHttpClient;

/**
 * Perform regular post updates in the background.
 */
public class UpdateService extends Service {
    static final String TAG = UpdateService.class.getSimpleName();
    static final String ACTION_UPDATE = "de.timbolender.fefereader.service.action.UPDATE";
    static final int NOTIFICATION_ID = 42; // What else?

    public static final String BROADCAST_UPDATE_SKIPPED = "de.timbolender.fefereader.service.action.UPDATE_SKIPPED";
    public static final String BROADCAST_UPDATE_FINISHED = "de.timbolender.fefereader.service.action.UPDATE_FINISHED";
    public static final String EXTRA_UPDATE_SUCCESS = "update_success";
    public static final int BROADCAST_PRIORITY_UI = 10;
    public static final int BROADCAST_PRIORITY_SERVICE = 0;

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
     * Start service which enables auto update.
     * @param context Context to use.
     */
    public static void startService(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        context.startService(intent);
    }

    /**
     * Trigger update in background service
     * @param context Context to use.
     */
    public static void startUpdate(Context context) {
        Intent updateIntent = createUpdateIntent(context);
        context.startService(updateIntent);
    }

    PreferenceHelper preferenceHelper;
    DatabaseWrapper databaseWrapper;
    BroadcastReceiver updateReceiver;
    Thread updateThread;

    @Override
    public void onCreate() {
        preferenceHelper = new PreferenceHelper(this);
        updateThread = null;

        // Get access to database
        SQLiteOpenHelper databaseHelper = new SQLiteOpenHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        databaseWrapper = new SQLiteWrapper(database);

        // Register broadcast receiver for notifications
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received update broadcast, no ui visible currently");

                // Only show notification if there is something to report
                boolean success = intent.getBooleanExtra(EXTRA_UPDATE_SUCCESS, false);
                long numNew = databaseWrapper.getUnreadPostCount();
                long numUpdated = databaseWrapper.getUpdatedPostCount();
                if(!success || (numNew == 0 && numUpdated == 0)) {
                    return;
                }

                // Show notification about update
                Log.d(TAG, "Showing notification");
                String newString = getResources()
                    .getQuantityString(R.plurals.notification_new_posts, (int) numNew, (int) numNew);
                String updatedString = getResources()
                    .getQuantityString(R.plurals.notification_updated_posts, (int) numUpdated, (int) numUpdated);
                String message = String.format(Locale.ENGLISH, "Es gibt %s und %s für dich.", newString, updatedString);
                if(numNew == 0) {
                    message = String.format(Locale.ENGLISH, "Es gibt %s für dich.", updatedString);
                }
                if(numUpdated == 0) {
                    message = String.format(Locale.ENGLISH, "Es gibt %s für dich.", newString);
                }

                Intent startIntent = new Intent(UpdateService.this, MainActivity.class);
                PendingIntent startPendingIntent = PendingIntent.getActivity(
                    UpdateService.this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(UpdateService.this)
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Neues von Fefe!")
                    .setContentText(message)
                    .setContentIntent(startPendingIntent)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(true);

                NotificationManager manager = (NotificationManager) UpdateService.this.getSystemService(NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION_ID, builder.build());
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_UPDATE_FINISHED);
        intentFilter.setPriority(BROADCAST_PRIORITY_SERVICE);
        registerReceiver(updateReceiver, intentFilter);

        // Register normal regular update behavior
        registerRegularUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Trigger update
        if(intent.getAction().equals(ACTION_UPDATE)) {
            Log.i(TAG, "Starting update");
            performUpdate();
        }

        return START_NOT_STICKY;
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

        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;

                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
                    Parser parser = new Parser();
                    Fetcher fetcher = new Fetcher(client, parser);
                    List<RawPost> posts = fetcher.fetch();

                    for(RawPost post : posts) {
                        databaseWrapper.addOrUpdatePost(post);
                    }

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
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, updateIntent, 0);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + updateInterval, updateInterval, pendingIntent);
        }
        else {
            Log.d(TAG, "No automatic updates desired");
        }
    }

}
