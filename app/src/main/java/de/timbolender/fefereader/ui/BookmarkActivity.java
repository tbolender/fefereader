package de.timbolender.fefereader.ui;

import android.os.Bundle;

import de.timbolender.fefereader.db.DatabaseWrapper;
import de.timbolender.fefereader.db.PostReader;

public class BookmarkActivity extends PostListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //
    // Override important methods
    //

    @Override
    PostReader getReader(DatabaseWrapper databaseWrapper) {
        return databaseWrapper.getPostsReader(DatabaseWrapper.FILTER_BOOKMARKED);
    }

    @Override
    boolean isUpdateOnStartEnabled() {
        return false;
    }

    @Override
    boolean isRefreshGestureEnabled() {
        return false;
    }
}
