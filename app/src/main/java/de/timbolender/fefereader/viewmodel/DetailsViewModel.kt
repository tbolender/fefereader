package de.timbolender.fefereader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import de.timbolender.fefereader.db.DataRepository

class DetailsViewModel(app: Application): AndroidViewModel(app) {
    private val repository: DataRepository = DataRepository(app)

    fun getPost(postId: String) =
            Transformations.map(repository.getPost(postId)) { post -> if (post != null) PostViewModel(post) else null }!!
}
