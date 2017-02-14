package de.timbolender.fefereader.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
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
import okhttp3.OkHttpClient;

/**
 * Perform regular post updates in the background.
 */
public class UpdateService extends Service {
    static final String TAG = UpdateService.class.getSimpleName();
    static final String ACTION_UPDATE = "de.timbolender.fefereader.service.action.UPDATE";
    static final int NOTIFICATION_ID = 42; // What else?

    public static final String BROADCAST_UPDATE_FINISHED = "de.timbolender.fefereader.service.action.UPDATE_FINISHED";
    public static final String EXTRA_UPDATE_SUCCESS = "update_success";
    public static final String EXTRA_NUM_NEW = "num_new";
    public static final String EXTRA_NUM_UPDATED = "num_updated";
    public static final int BROADCAST_PRIORITY_UI = 10;
    public static final int BROADCAST_PRIORITY_SERVICE = 0;

    /**
     * Trigger update in background service
     * @param context Context to use.
     */
    public static void startUpdate(Context context) {
        Intent updateIntent = new Intent(context, UpdateService.class);
        updateIntent.setAction(ACTION_UPDATE);
        context.startService(updateIntent);
    }

    DatabaseWrapper databaseWrapper;
    BroadcastReceiver updateReceiver;
    Thread updateThread;

    @Override
    public void onCreate() {
        updateThread = null;

        // Get access to database
        SQLiteOpenHelper databaseHelper = new SQLiteOpenHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        databaseWrapper = new SQLiteWrapper(database);

        // Register broadcast receiver for notifications
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received update broadcast, displaying notification");

                // Only show notification if there is something to report
                boolean success = intent.getBooleanExtra(EXTRA_UPDATE_SUCCESS, false);
                int numNew = intent.getIntExtra(EXTRA_NUM_NEW, 0);
                int numUpdated = intent.getIntExtra(EXTRA_NUM_UPDATED, 0);
                if(!success || (numNew == 0 && numUpdated == 0)) {
                    return;
                }

                // Show notification about update
                String message = String.format(Locale.ENGLISH, "Es warten %d neue Posts und %d Updates auf dich.", numNew, numUpdated);
                if(numNew == 0) {
                    message = String.format(Locale.ENGLISH, "Es warten %d Updates auf dich.", numUpdated);
                }
                if(numUpdated == 0) {
                    message = String.format(Locale.ENGLISH, "Es warten %d neue Posts auf dich.", numUpdated);
                }

                Intent startIntent = new Intent(UpdateService.this, MainActivity.class);
                PendingIntent startPendingIntent = PendingIntent.getActivity(
                    UpdateService.this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(UpdateService.this)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Neues von Fefe!")
                    .setContentText(message)
                    .setContentIntent(startPendingIntent);

                NotificationManager manager = (NotificationManager) UpdateService.this.getSystemService(NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION_ID, builder.build());
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_UPDATE_FINISHED);
        intentFilter.setPriority(BROADCAST_PRIORITY_SERVICE);
        registerReceiver(updateReceiver, intentFilter);

        // TODO: Register normal regular update behavior
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Trigger update
        if(intent.getAction().equals(ACTION_UPDATE)) {
            Log.i(TAG, "Starting post update");
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
            return;
        }

        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                int numCreated = 0;
                int numUpdated = 0;

                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
                    Parser parser = new Parser();
                    Fetcher fetcher = new Fetcher(client, parser);
                    List<RawPost> posts = fetcher.fetch();

                    for(RawPost post : posts) {
                        DatabaseWrapper.DatabaseOperation result = databaseWrapper.addOrUpdatePost(post);
                        numCreated += (result == DatabaseWrapper.DatabaseOperation.CREATED) ? 1 : 0;
                        numUpdated += (result == DatabaseWrapper.DatabaseOperation.UPDATED) ? 1 : 0;
                    }

                    success = true;
                }
                catch(ParseException | IOException e) {
                    e.printStackTrace();
                }
                finally {
                    Intent finishedIntent = new Intent(BROADCAST_UPDATE_FINISHED);
                    finishedIntent.putExtra(EXTRA_UPDATE_SUCCESS, success);
                    if(success) {
                        finishedIntent.putExtra(EXTRA_NUM_NEW, numCreated);
                        finishedIntent.putExtra(EXTRA_NUM_UPDATED, numUpdated);
                    }
                    sendOrderedBroadcast(finishedIntent, null);

                    updateThread = null;
                }
            }
        });
        updateThread.start();
    }

}
