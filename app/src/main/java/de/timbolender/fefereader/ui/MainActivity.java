package de.timbolender.fefereader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.db.DatabaseWrapper;
import de.timbolender.fefereader.db.PostReader;
import de.timbolender.fefereader.service.UpdateService;

/**
 * Main activity displaying all retrieved posts
 */
public class MainActivity extends PostListActivity {
    private static final String FILTER_UNREAD_KEY = "filter_unread";

    boolean filterUnread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore filter state
        filterUnread = false;
        if(savedInstanceState != null) {
            filterUnread = savedInstanceState.getBoolean(FILTER_UNREAD_KEY);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(FILTER_UNREAD_KEY, filterUnread);
    }

    //
    // Override important methods
    //

    @Override
    PostReader getReader(DatabaseWrapper databaseWrapper) {
        int filter = filterUnread ? DatabaseWrapper.FILTER_UNREAD : DatabaseWrapper.FILTER_NONE;
        return databaseWrapper.getPostsReader(filter);
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
        MenuItem unreadItem = menu.findItem(R.id.menu_unread_filter);
        unreadItem.setTitle(filterUnread ? R.string.menu_unread_filter_undo : R.string.menu_unread_filter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.menu_mark_read) {
            onMarkAllAsReadClick();
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
            onUnreadFilterToggle();
            return true;
        }
        if(itemId == R.id.menu_settings) {
            onSettingsClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onMarkAllAsReadClick() {
        PostReader reader = getReader(databaseWrapper);
        for(int i = 0; i < reader.getCount(); i++) {
            Post post = reader.get(i);
            if(!post.isRead() || post.isUpdated()) {
                databaseWrapper.setRead(post.getId(), true);
            }
        }

        updateAdapter();
    }

    private void onBookmarkFilterClick() {
        Intent filterIntent = new Intent(this, BookmarkActivity.class);
        startActivity(filterIntent);
    }

    private void onUpdateClick() {
        refreshLayout.setRefreshing(true);
        UpdateService.startUpdate(this);
    }

    private void onUnreadFilterToggle() {
        filterUnread = !filterUnread;
        updateAdapter();
        invalidateOptionsMenu();
    }

    private void onSettingsClick() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
