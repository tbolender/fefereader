package de.timbolender.fefereader.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import de.timbolender.fefereader.db.Post;
import de.timbolender.fefereader.viewmodel.UnreadViewModel;

public class UnreadActivity extends PostListActivity {
    @Override
    LiveData<PagedList<Post>> getPostPagedList() {
        UnreadViewModel vm = new ViewModelProvider(this).get(UnreadViewModel.class);
        return vm.getPostsPaged();
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
