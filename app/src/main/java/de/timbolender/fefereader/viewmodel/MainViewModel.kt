package de.timbolender.fefereader.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.timbolender.fefereader.db.DataRepository
import java.util.concurrent.Executors

class MainViewModel(app: Application): AndroidViewModel(app) {
    companion object {
        private val TAG: String = MainViewModel::class.simpleName!!
    }

    private val repository: DataRepository = DataRepository(app)

    val postsPaged: LiveData<PagedList<PostViewModel>>
        init {
            val factory = repository.getAllPostsPaged().map { post -> PostViewModel(post) }
            postsPaged = LivePagedListBuilder<Int, PostViewModel>(factory, 20).build()
        }

    fun markAllPostsAsRead() {
        Log.d(TAG, "Mark all posts as read")
        Executors.newSingleThreadScheduledExecutor().execute {
            for (post in repository.getUnreadPostsSync()) {
                repository.markPostAsReadSync(post.id)
            }
        }
    }
}
