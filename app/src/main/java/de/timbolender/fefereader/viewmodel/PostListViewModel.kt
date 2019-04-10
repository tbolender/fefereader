package de.timbolender.fefereader.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import de.timbolender.fefereader.db.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostListViewModel(app: Application): AndroidViewModel(app) {
    companion object {
        private val TAG: String = PostListViewModel::class.simpleName!!
    }

    private val repository: DataRepository = DataRepository(app)

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun markPostAsRead(postId: String) =
        ioScope.launch {
            Log.d(TAG, "Mark post $postId as read")
            repository.markPostAsReadSync(postId)
        }

    fun togglePostBookmark(postId: String) =
        ioScope.launch {
            Log.d(TAG, "Toggle post $postId bookmark")
            repository.togglePostBookmarkSync(postId)
        }
}
