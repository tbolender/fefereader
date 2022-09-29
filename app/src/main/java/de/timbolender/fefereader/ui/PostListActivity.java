package de.timbolender.fefereader.ui;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.databinding.ActivityPostListBinding;
import de.timbolender.fefereader.db.Post;
import de.timbolender.fefereader.background.UpdateWorker;
import de.timbolender.fefereader.ui.helper.NotificationSilencer;
import de.timbolender.fefereader.viewmodel.PostListViewModel;

/**
 * Base activity class featuring a list of posts with default actions.
 */
public abstract class PostListActivity extends AppCompatActivity implements PostPagedAdapter.OnPostSelectedListener  {
    static final String TAG = PostListActivity.class.getSimpleName();

    ActivityPostListBinding binding;
    PostListViewModel vm;

    boolean shouldPerformUpdate;

    NotificationSilencer updateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prepare ui
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_list);
        vm = new ViewModelProvider(this).get(PostListViewModel.class);

        // Prepare list view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.postList.addItemDecoration(dividerItemDecoration);

        // Fill content
        PostPagedAdapter postAdapter = new PostPagedAdapter(this);
        getPostPagedList().observe(this, postAdapter::submitList);
        binding.postList.setAdapter(postAdapter);

        // Handle swipe update gesture
        if(isRefreshGestureEnabled()) {
            binding.refreshLayout.setOnRefreshListener(() -> UpdateWorker.Companion.startManualUpdate(this));
            binding.refreshLayout.setColorSchemeResources(R.color.colorAccent);
        }

        // Create receiver to consume update notifications
        updateReceiver = new NotificationSilencer();

        // Create receiver for manual updates
        WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData(UpdateWorker.MANUAL_UPDATE_WORKER)
            .observe(this, workInfo -> {
                // Extract state
                if(workInfo.isEmpty()) return;
                WorkInfo.State state = workInfo.get(0).getState();

                // Set correct refresh layout state
                if(state == WorkInfo.State.RUNNING) {
                    binding.refreshLayout.setRefreshing(true);
                }

                // Skip non-interesting info
                if(state != WorkInfo.State.SUCCEEDED && state != WorkInfo.State.FAILED) return;

                Log.d(TAG, "Received new update worker info");
                if(state == WorkInfo.State.FAILED) {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_LONG).show();
                }
                binding.refreshLayout.setRefreshing(false);

                // Scroll to top to show latest results
                if(state == WorkInfo.State.SUCCEEDED) {
                    binding.postList.scrollToPosition(0);
                }
            });

        // Trigger update if desired
        shouldPerformUpdate = isUpdateOnStartEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateReceiver.register(this);

        // Drop all user notifications
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        // Trigger update if desired
        binding.refreshLayout.setRefreshing(false);
        if(shouldPerformUpdate) {
            UpdateWorker.Companion.startManualUpdate(this);
            shouldPerformUpdate = false;
        }
    }

    @Override
    protected void onPause() {
        updateReceiver.unregister(this);

        super.onPause();
    }

    //
    // Behavior determining methods
    //

    abstract LiveData<PagedList<Post>> getPostPagedList();

    abstract boolean isUpdateOnStartEnabled();

    abstract boolean isRefreshGestureEnabled();

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
