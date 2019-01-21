package de.timbolender.fefereader.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import de.timbolender.fefereader.viewmodel.BookmarkViewModel;
import de.timbolender.fefereader.viewmodel.PostViewModel;

public class BookmarkActivity extends PostListActivity {
    @Override
    LiveData<PagedList<PostViewModel>> getPostPagedList() {
        BookmarkViewModel vm = ViewModelProviders.of(this).get(BookmarkViewModel.class);
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
