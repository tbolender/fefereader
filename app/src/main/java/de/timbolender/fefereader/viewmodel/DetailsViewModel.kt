package de.timbolender.fefereader.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import de.timbolender.fefereader.db.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(app: Application): AndroidViewModel(app) {
    companion object {
        private val TAG: String = DetailsViewModel::class.simpleName!!
    }

    private val repository: DataRepository = DataRepository(app)

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun getPost(postId: String) = repository.getPost(postId)

    fun toggleBookmark(postId: String) =
        ioScope.launch {
            Log.d(TAG, "Toggle post $postId bookmark")
            repository.togglePostBookmarkSync(postId)
        }
}
