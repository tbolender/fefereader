package de.timbolender.fefereader.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.db.Post
import de.timbolender.fefereader.network.Updater
import de.timbolender.fefereader.viewmodel.SearchViewModel
import java.io.IOException
import java.text.ParseException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SearchableActivity : PostListActivity() {
    public override fun getPostPagedList(): LiveData<PagedList<Post>> {
        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH != intent.action) {
            throw RuntimeException()
        }
        val query = intent.getStringExtra(SearchManager.QUERY) ?: throw RuntimeException()

        title = String.format("Suchergebnisse: %s", query)
        val repository = DataRepository(this)
        val updater = Updater(repository)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CompletableFuture.runAsync {
                try {
                    updater.update(query)
                } catch (e: ParseException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            val executor: Executor = Executors.newSingleThreadExecutor()
            executor.execute {
                try {
                    updater.update(query)
                } catch (e: ParseException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        val vm = ViewModelProvider(this)[SearchViewModel::class.java]
        return vm.getPostsPaged(query)
    }

    public override fun isUpdateOnStartEnabled(): Boolean {
        return true
    }

    public override fun isRefreshGestureEnabled(): Boolean {
        return false
    }
}