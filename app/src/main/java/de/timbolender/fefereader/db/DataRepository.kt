package de.timbolender.fefereader.db

import android.app.Application
import androidx.lifecycle.LiveData

/**
 * Central data access class. Automatically triggers updates if required.
 */
class DataRepository(application: Application) {
    private val db = AppDatabase.getInstance(application)
    private val postDao = db.postDao()

    fun createOrUpdatePostSync(post: Post): Long {
        return postDao.insertPosts(post)[0]
    }

    fun getPost(postId: String): LiveData<Post?> {
        return postDao.loadPost(postId)
    }

    fun getPostSync(postId: String): Post? {
        return postDao.loadPostSync(postId)
    }
}
