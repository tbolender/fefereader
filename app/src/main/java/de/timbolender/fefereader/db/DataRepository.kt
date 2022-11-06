package de.timbolender.fefereader.db

import android.content.Context
import androidx.paging.DataSource
import androidx.sqlite.db.SimpleSQLiteQuery

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

    // Tries yield the same results as /?q= on fefes blog:
    //   1. Search is case sensitive
    //   2. Space equals AND: All words must be contained anywhere in the result.
    fun findPostsByContent(sentence: String): DataSource.Factory<Int, Post> {
        val words = sentence.split(" ").map { "*$it*" }
        if(words.size == 1) {
            return postDao.findPostsPaged(words.first())
        }
        var sqlStatement = "SELECT * FROM post WHERE contents GLOB ?"
        for (word in words.drop(1)) {
            sqlStatement += " AND contents GLOB ?"
        }
        sqlStatement += " ORDER BY date DESC, timestampId DESC"
        return postDao.findPostsPaged(SimpleSQLiteQuery(sqlStatement, words.toTypedArray()))
    }

}
