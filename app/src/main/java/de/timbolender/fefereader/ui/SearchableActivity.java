package de.timbolender.fefereader.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import de.timbolender.fefereader.db.DataRepository;
import de.timbolender.fefereader.db.Post;
import de.timbolender.fefereader.network.Updater;
import de.timbolender.fefereader.viewmodel.SearchViewModel;

public class SearchableActivity extends PostListActivity {

    @Override
    LiveData<PagedList<Post>> getPostPagedList() {
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (!Intent.ACTION_SEARCH.equals(intent.getAction())) {
            throw new RuntimeException();
        }
        String query = intent.getStringExtra(SearchManager.QUERY);
        setTitle(String.format("Suchergebnisse: %s", query));
        DataRepository repository = new DataRepository(this);
        Updater updater = new Updater(repository);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CompletableFuture.runAsync(() -> {
                try {
                    updater.update(query);
                }
                catch(ParseException | IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    updater.update(query);
                }
                catch(ParseException | IOException e) {
                    e.printStackTrace();
                }
            });
        }
        SearchViewModel vm = new ViewModelProvider(this).get(SearchViewModel.class);
        return vm.getPostsPaged(query);
    }

    @Override
    boolean isUpdateOnStartEnabled() {
        return true;
    }

    @Override
    boolean isRefreshGestureEnabled() {
        return false;
    }

}

