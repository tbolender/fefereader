package de.timbolender.fefereader.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.service.UpdateService;
import de.timbolender.fefereader.viewmodel.MainViewModel;
import de.timbolender.fefereader.viewmodel.PostViewModel;

/**
 * Main activity displaying all retrieved posts
 */
public class MainActivity extends PostListActivity {
    MainViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        vm = ViewModelProviders.of(this).get(MainViewModel.class);
        super.onCreate(savedInstanceState);
    }

    //
    // Override important methods
    //

    @Override
    LiveData<PagedList<PostViewModel>> getPostPagedList() {
        return vm.getPostsPaged();
    }

    @Override
    boolean isUpdateOnStartEnabled() {
        return true;
    }

    @Override
    boolean isRefreshGestureEnabled() {
        return true;
    }

    //
    // Menu handling
    //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.menu_mark_read) {
            vm.markAllPostsAsRead();
            return true;
        }
        if(itemId == R.id.menu_bookmark_filter) {
            onBookmarkFilterClick();
            return true;
        }
        if(itemId == R.id.menu_refresh) {
            onUpdateClick();
            return true;
        }
        if(itemId == R.id.menu_unread_filter) {
            onUnreadFilterClick();
            return true;
        }
        if(itemId == R.id.menu_feedback_mail) {
            onFeedbackMailClick();
            return true;
        }
        if(itemId == R.id.menu_settings) {
            onSettingsClick();
            return true;
        }
        if(itemId == R.id.menu_about) {
            onAboutClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onBookmarkFilterClick() {
        Intent filterIntent = new Intent(this, BookmarkActivity.class);
        startActivity(filterIntent);
    }

    private void onUpdateClick() {
        binding.refreshLayout.setRefreshing(true);
        UpdateService.startManualUpdate(this);
    }

    private void onUnreadFilterClick() {
        Intent filterIntent = new Intent(this, UnreadActivity.class);
        startActivity(filterIntent);
    }

    private void onFeedbackMailClick() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@timbolender.de"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback Fefe News");
        startActivity(intent);
    }

    private void onAboutClick() {
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
    }

    private void onSettingsClick() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
