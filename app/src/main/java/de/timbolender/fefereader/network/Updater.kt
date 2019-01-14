package de.timbolender.fefereader.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import de.timbolender.fefereader.db.DatabaseWrapper
import okhttp3.OkHttpClient
import java.io.IOException
import java.text.ParseException

class Updater(private val databaseWrapper: DatabaseWrapper) {
    @Throws(ParseException::class, IOException::class)
    fun update() {
        val client = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()

        val parser = Parser()
        val fetcher = Fetcher(client, parser)
        val posts = fetcher.fetch()

        for (post in posts) {
            databaseWrapper.addOrUpdatePost(post)
        }
    }
}
