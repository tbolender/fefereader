package de.timbolender.fefereader.db

import android.app.Application

/**
 * Central data access class. Automatically triggers updates if required.
 */
class DataRepository(application: Application) {
    private val db = AppDatabase.getInstance(application)
    private val postDao = db.postDao()

    fun createPost(post: Post): Long {
        return postDao.insertPosts(post)[0]
    }
}
