package de.timbolender.fefereader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
    SQLiteOpenHelper databaseHelper;
    PostAdapter postAdapter;
    DatabaseWrapper databaseWrapper;

    @BindView(R.id.post_list) RecyclerView postList;

    BroadcastReceiver updateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        databaseHelper = new SQLiteOpenHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        databaseWrapper = new SQLiteWrapper(database);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        postList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        postList.addItemDecoration(dividerItemDecoration);

        // Fill content
        PostReader reader = databaseWrapper.getPostsReader();
        postAdapter = new PostAdapter(reader, this);
        postList.setAdapter(postAdapter);

        // Create receiver for updates
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateAdapter();
                Toast.makeText(MainActivity.this, "Feed aktualisiert", Toast.LENGTH_SHORT).show();
                abortBroadcast();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_refresh) {
            UpdateService.startUpdate(this);
            return true;
        }
        else if(item.getItemId() == R.id.menu_mark_read) {
            onMarkAllAsReadClick();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    public void onMarkAllAsReadClick() {
        PostReader reader = databaseWrapper.getPostsReader();
        for(int i = 0; i < reader.getCount(); i++) {
            Post post = reader.get(i);
            if(!post.isRead() || post.isUpdated()) {
                databaseWrapper.markRead(post);
            }
        }

        updateAdapter();
    }

    private void updateAdapter() {
        PostReader reader = databaseWrapper.getPostsReader();
        postAdapter.setReader(reader);
    }

    @Override
    public void OnPostSelected(Post post) {
        databaseWrapper.markRead(post);
        updateAdapter();

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.INTENT_EXTRA_POST_CONTENT, post.getContents());
        startActivity(intent);
    }
}
