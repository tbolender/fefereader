package de.timbolender.fefereader.ui;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.db.DatabaseWrapper;
import de.timbolender.fefereader.db.PostReader;
import de.timbolender.fefereader.db.SQLiteOpenHelper;
import de.timbolender.fefereader.db.SQLiteWrapper;
import de.timbolender.fefereader.service.UpdateService;

public class MainActivity extends AppCompatActivity implements PostAdapter.OnPostSelectedListener {
    static final String TAG = MainActivity.class.getSimpleName();

    SQLiteOpenHelper databaseHelper;
    PostAdapter postAdapter;
    DatabaseWrapper databaseWrapper;

    @BindView(R.id.post_list) RecyclerView postList;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;

    BroadcastReceiver updateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Set up data base
        databaseHelper = new SQLiteOpenHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        databaseWrapper = new SQLiteWrapper(database);

        // Prepare list view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        postList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        postList.addItemDecoration(dividerItemDecoration);

        // Fill content
        PostReader reader = databaseWrapper.getPostsReader();
        postAdapter = new PostAdapter(reader, this);
        postList.setAdapter(postAdapter);

        // Handle swipe update gesture
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UpdateService.startUpdate(MainActivity.this);
            }
        });
        refreshLayout.setColorSchemeResources(R.color.colorAccent);

        // Create receiver for updates
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received content update notification");
                refreshLayout.setRefreshing(false);
                updateAdapter();
                abortBroadcast();
            }
        };

        // Trigger update on beginning
        onUpdateClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_mark_read) {
            onMarkAllAsReadClick();
            return true;
        }
        if(item.getItemId() == R.id.menu_refresh) {
            onUpdateClick();
            return true;
        }
        if(item.getItemId() == R.id.menu_settings) {
            onSettingsClick();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Make sure we have the latest content
        updateAdapter();

        // Drop all user notifications
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        // Register broadcast receiver for notifications
        IntentFilter intentFilter = new IntentFilter(UpdateService.BROADCAST_UPDATE_FINISHED);
        intentFilter.setPriority(UpdateService.BROADCAST_PRIORITY_UI);
        registerReceiver(updateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(updateReceiver);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        databaseWrapper.cleanUp();
        databaseHelper.close();
        super.onDestroy();
    }

    private void onUpdateClick() {
        refreshLayout.setRefreshing(true);
        UpdateService.startUpdate(this);
    }

    private void onMarkAllAsReadClick() {
        PostReader reader = databaseWrapper.getPostsReader();
        for(int i = 0; i < reader.getCount(); i++) {
            Post post = reader.get(i);
            if(!post.isRead() || post.isUpdated()) {
                databaseWrapper.setRead(post.getId(), true);
            }
        }

        updateAdapter();
    }

    private void onSettingsClick() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void updateAdapter() {
        PostReader reader = databaseWrapper.getPostsReader();
        postAdapter.setReader(reader);
    }

    @Override
    public void OnPostSelected(Post post) {
        databaseWrapper.setRead(post.getId(), true);
        updateAdapter();

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.INTENT_EXTRA_POST, post);
        startActivity(intent);
    }

    @Override
    public void OnPostLongPressed(Post post) {
        databaseWrapper.setBookmarked(post.getId(), !post.isBookmarked());
        updateAdapter();
    }
}
