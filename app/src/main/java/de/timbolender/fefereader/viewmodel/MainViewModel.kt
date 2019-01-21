package de.timbolender.fefereader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.timbolender.fefereader.db.DataRepository

class MainViewModel(app: Application): AndroidViewModel(app) {
    private val repository: DataRepository = DataRepository(app)

    val postsPaged: LiveData<PagedList<PostViewModel>>
        init {
            val factory = repository.getAllPostsPaged().map { post ->
                PostViewModel(post.id, post.timestampId, post.isRead, post.isUpdated, post.isBookmarked, post.contents, post.date)
            }
            postsPaged = LivePagedListBuilder<Int, PostViewModel>(factory, 20).build()
        }
}
