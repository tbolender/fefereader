package de.timbolender.fefereader.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.db.Post
import okhttp3.OkHttpClient
import java.io.IOException
import java.text.ParseException
import java.util.*

class Updater(private val repository: DataRepository) {
    @Throws(ParseException::class, IOException::class)
    fun update() {
        val client = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()

        val parser = Parser()
        val fetcher = Fetcher(client, parser)
        val posts = fetcher.fetch()

        for (post in posts) {
            val dbPost = Post(
                    0, post.timestampId, false, false, false,
                    post.contents, Date(post.date))
            repository.createPost(dbPost)
        }
    }
}
