package de.timbolender.fefereader.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.paging.toLiveData
import de.timbolender.fefereader.db.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(app: Application): AndroidViewModel(app) {
    companion object {
        private val TAG: String = MainViewModel::class.simpleName!!
    }

    private val repository: DataRepository = DataRepository(app)
    val postsPaged = repository.getAllPostsPaged().toLiveData(20)

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun markAllPostsAsRead() {
        ioScope.launch {
            Log.d(TAG, "Mark all posts as read")
            for (post in repository.getUnreadPostsSync()) {
                repository.markPostAsReadSync(post.id)
            }
        }
    }
}
