package de.timbolender.fefereader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.toLiveData
import de.timbolender.fefereader.db.DataRepository

class UnreadViewModel(app: Application): AndroidViewModel(app) {
    private val repository: DataRepository = DataRepository(app)

    val postsPaged = repository.getUnreadPostsPaged().toLiveData(20)
}
