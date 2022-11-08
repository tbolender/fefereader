package de.timbolender.fefereader.network

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.db.Post
import okhttp3.OkHttpClient
import java.io.IOException
import java.text.ParseException
import java.util.*

class Updater(private val repository: DataRepository) {
    companion object {
        private var TAG: String = Updater::class.simpleName!!
    }

    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor())
        .build()
    private val parser = Parser()
    private val fetcher = Fetcher(client)

    @Throws(ParseException::class, IOException::class)
    fun update() {
        val postSoup = fetcher.fetch()
        val rawPosts = parser.parse(postSoup)
        rawPosts.forEach {  createOrUpdate(it) }
    }

    @Throws(ParseException::class, IOException::class)
    fun update(query: String) {
        val postSoup = fetcher.fetch(query)
        val rawPosts = parser.parse(postSoup)
        Log.d(TAG, "Found ${rawPosts.size} posts matching '$query'")
        rawPosts.forEach {  createOrUpdate(it) }
    }

    private fun createOrUpdate(post: RawPost) {
        val existingPost = repository.getPostSync(post.id)
        if(existingPost == null) {
            Log.d(TAG, "Inserted new post ${post.id}")
            val dbPost = Post(post.id, post.timestampId,
                isRead = false,
                isUpdated = false,
                isBookmarked = false,
                contents = post.contents,
                date = Date(post.date)
            )
            repository.createOrUpdatePostSync(dbPost)
        }
        else if(existingPost.contents != post.contents) {
            Log.d(TAG, "Updated post ${post.id}")
            val dbPost = existingPost.copy(contents = post.contents, isRead = false, isUpdated = true)
            repository.createOrUpdatePostSync(dbPost)
        }
    }
}
