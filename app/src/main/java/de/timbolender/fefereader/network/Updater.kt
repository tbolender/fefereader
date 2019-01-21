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

    @Throws(ParseException::class, IOException::class)
    fun update() {
        val client = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()

        val parser = Parser()
        val fetcher = Fetcher(client, parser)
        val posts = fetcher.fetch()

        for (post in posts) {
            val existingPost = repository.getPostSync(post.id)
            val dbPost = if(existingPost == null) {
                Log.d(TAG, "Inserted new post ${post.id}")
                Post(post.id, post.timestampId, false, false, false, post.contents, Date(post.date))
            }
            else {
                Log.d(TAG, "Updated post ${post.id}")
                existingPost.copy(contents = post.contents, isUpdated = true)
            }
            repository.createOrUpdatePostSync(dbPost)
        }
    }
}
