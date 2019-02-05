package de.timbolender.fefereader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.toLiveData
import de.timbolender.fefereader.db.DataRepository

class BookmarkViewModel(app: Application): AndroidViewModel(app) {
    private val repository: DataRepository = DataRepository(app)

    val postsPaged = repository.getBookmarkedPostsPaged().toLiveData(20)
}
