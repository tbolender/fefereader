package de.timbolender.fefereader.ui;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.databinding.ActivityPostListBinding;
import de.timbolender.fefereader.db.Post;
import de.timbolender.fefereader.service.UpdateService;
import de.timbolender.fefereader.viewmodel.PostListViewModel;

/**
 * Base activity class featuring a list of posts with default actions.
 */
public abstract class PostListActivity extends AppCompatActivity implements PostPagedAdapter.OnPostSelectedListener  {
    static final String TAG = PostListActivity.class.getSimpleName();

    ActivityPostListBinding binding;
    PostListViewModel vm;

    boolean shouldPerformUpdate;

    BroadcastReceiver updateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prepare ui
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_list);
        vm = ViewModelProviders.of(this).get(PostListViewModel.class);

        // Prepare list view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.postList.addItemDecoration(dividerItemDecoration);

        // Fill content
        PostPagedAdapter postAdapter = new PostPagedAdapter(this);
        getPostPagedList().observe(this, postAdapter::submitList);
        binding.postList.setAdapter(postAdapter);

        // Handle swipe update gesture
        if(isRefreshGestureEnabled()) {
            binding.refreshLayout.setOnRefreshListener(() -> UpdateService.startManualUpdate(PostListActivity.this));
            binding.refreshLayout.setColorSchemeResources(R.color.colorAccent);
        }

        // Create receiver for updates
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received content update notification");
                binding.refreshLayout.setRefreshing(false);
                abortBroadcast();
            }
        };

        // Trigger update if desired
        shouldPerformUpdate = isUpdateOnStartEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Drop all user notifications
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        // Register broadcast receiver for notifications
        IntentFilter updateFilter = new IntentFilter(UpdateService.BROADCAST_UPDATE_FINISHED);
        updateFilter.setPriority(UpdateService.BROADCAST_PRIORITY_UI);
        registerReceiver(updateReceiver, updateFilter);
        IntentFilter skippedFilter = new IntentFilter(UpdateService.BROADCAST_UPDATE_SKIPPED);
        registerReceiver(updateReceiver, skippedFilter);

        // Trigger update if desired
        binding.refreshLayout.setRefreshing(false);
        if(shouldPerformUpdate) {
            requestUpdate();
            shouldPerformUpdate = false;
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(updateReceiver);

        super.onPause();
    }

    //
    // Behavior determining methods
    //

    abstract LiveData<PagedList<Post>> getPostPagedList();

    abstract boolean isUpdateOnStartEnabled();

    abstract boolean isRefreshGestureEnabled();

    //
    // General utility functions
    //

    void requestUpdate() {
        binding.refreshLayout.setRefreshing(true);
        UpdateService.startManualUpdate(this);
    }

    //
    // Post entry handling
    //

    @Override
    public void onPostSelected(@NotNull String postId) {
        vm.markPostAsRead(postId);
        Intent intent = DetailsActivity.createShowPostIntent(this, postId);
        startActivity(intent);
    }

    @Override
    public boolean onPostLongPressed(@NotNull String postId) {
        vm.togglePostBookmark(postId);
        return true;
    }
}
