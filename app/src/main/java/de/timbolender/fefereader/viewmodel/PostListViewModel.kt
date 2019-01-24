package de.timbolender.fefereader.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import de.timbolender.fefereader.db.DataRepository
import java.util.concurrent.Executors

class PostListViewModel(app: Application): AndroidViewModel(app) {
    companion object {
        private val TAG: String = PostListViewModel::class.simpleName!!
    }

    private val repository: DataRepository = DataRepository(app)

    fun markPostAsRead(postId: String) {
        Log.d(TAG, "Mark post ${postId} as read")
        Executors.newSingleThreadScheduledExecutor().execute {
            repository.markPostAsReadSync(postId)
        }
    }

    fun togglePostBookmark(postId: String) {
        Log.d(TAG, "Toggle post ${postId} bookmark")
        Executors.newSingleThreadScheduledExecutor().execute {
            repository.togglePostBookmark(postId)
        }
    }
}
