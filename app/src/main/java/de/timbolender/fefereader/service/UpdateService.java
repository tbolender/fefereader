package de.timbolender.fefereader.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.timbolender.fefereader.data.RawPost;
import de.timbolender.fefereader.db.DatabaseWrapper;
import de.timbolender.fefereader.db.SQLiteOpenHelper;
import de.timbolender.fefereader.db.SQLiteWrapper;
import de.timbolender.fefereader.network.Fetcher;
import de.timbolender.fefereader.network.Parser;
import okhttp3.OkHttpClient;

/**
 * Perform regular post updates in the background.
 */
public class UpdateService extends Service {
    static final String TAG = UpdateService.class.getSimpleName();
    static final String ACTION_UPDATE = "de.timbolender.fefereader.service.action.UPDATE";

    public static final String BROADCAST_UPDATE_FINISHED = "de.timbolender.fefereader.service.action.UPDATE_FINISHED";
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
                // TODO: Show notification about update
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
            Log.i(TAG, "Starting post update.");
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
                try {
                    OkHttpClient client = new OkHttpClient();
                    Parser parser = new Parser();
                    Fetcher fetcher = new Fetcher(client, parser);
                    List<RawPost> posts = fetcher.fetch();

                    for(RawPost post : posts) {
                        databaseWrapper.addOrUpdatePost(post);
                    }

                    Intent finishedIntent = new Intent(BROADCAST_UPDATE_FINISHED);
                    sendBroadcast(finishedIntent);
                }
                catch(ParseException | IOException e) {
                    e.printStackTrace();
                }
                finally {
                    updateThread = null;
                }
            }
        });
        updateThread.start();
    }

}
