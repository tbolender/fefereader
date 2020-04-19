package de.timbolender.fefereader.db

import android.content.Context

/**
 * Central data access class. Automatically triggers updates if required.
 */
class DataRepository(application: Context) {
    private val db = AppDatabase.getInstance(application)
    private val postDao = db.postDao()

    fun createOrUpdatePostSync(post: Post) = postDao.insertPosts(post)[0]

    fun getPost(postId: String) = postDao.loadPost(postId)

    fun getPostSync(postId: String) = postDao.loadPostSync(postId)

    fun getAllPostsPaged() = postDao.loadAllPostsPaged()

    fun getBookmarkedPostsPaged() = postDao.loadBookmarkedPostsPaged()

    fun getUnreadPostsPaged() = postDao.loadUnreadPostsPaged()

    fun getUnreadPostsSync() = postDao.loadUnreadPostsSync()

    val newPostsCount
        get() = postDao.loadNewCount()

    val updatedPostsCount
        get() = postDao.loadUpdatedCount()

    fun markPostAsReadSync(postId: String) = postDao.markAsReadSync(postId)

    fun togglePostBookmarkSync(postId: String) = postDao.toggleBookmarkSync(postId)
}
