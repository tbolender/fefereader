package de.timbolender.fefereader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.db.Post

class SearchViewModel(app: Application): AndroidViewModel(app) {
    private val repository: DataRepository = DataRepository(app)

    fun getPostsPaged(query: String): LiveData<PagedList<Post>> {
        return repository.findPostsByContent(query).toLiveData(20)
    }

}
