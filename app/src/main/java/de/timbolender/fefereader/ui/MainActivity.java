package de.timbolender.fefereader.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.db.Post;
import de.timbolender.fefereader.background.UpdateWorker;
import de.timbolender.fefereader.util.PreferenceHelper;
import de.timbolender.fefereader.viewmodel.MainViewModel;

/**
 * Main activity displaying all retrieved posts
 */
public class MainActivity extends PostListActivity {
    MainViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(MainViewModel.class);
        super.onCreate(savedInstanceState);

        PreferenceHelper preferenceHelper = new PreferenceHelper(this);
        UpdateWorker.Companion.configureAutomaticUpdates(this, preferenceHelper);
    }

    //
    // Override important methods
    //

    @Override
    LiveData<PagedList<Post>> getPostPagedList() {
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
        setUpSearch(menu);
        return true;
    }

    private void setUpSearch(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        ComponentName searchComponent = new ComponentName(this, SearchableActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(searchComponent));
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
        if(itemId == R.id.menu_homepage) {
            onHomepageClick();
            return true;
        }
        if(itemId == R.id.menu_contact) {
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
        UpdateWorker.Companion.startManualUpdate(this);
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

    private void onHomepageClick() {
        String homepage = "https://github.com/tbolender/fefereader";
        Intent openIntent = new Intent(Intent.ACTION_VIEW);
        openIntent.setData(Uri.parse(homepage));
        startActivity(openIntent);
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
