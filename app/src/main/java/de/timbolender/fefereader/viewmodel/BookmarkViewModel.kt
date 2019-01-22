package de.timbolender.fefereader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.timbolender.fefereader.db.DataRepository

class BookmarkViewModel(app: Application): AndroidViewModel(app) {
    private val repository: DataRepository = DataRepository(app)

    val postsPaged: LiveData<PagedList<PostViewModel>>
        init {
            val factory = repository.getBookmarkedPostsPaged().map { post -> PostViewModel(post) }
            postsPaged = LivePagedListBuilder<Int, PostViewModel>(factory, 20).build()
        }
}
