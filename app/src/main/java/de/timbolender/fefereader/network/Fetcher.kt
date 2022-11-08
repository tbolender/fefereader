package de.timbolender.fefereader.network

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.ParseException

/**
 * Fetched current front page of Fefe's blog.
 */
class Fetcher(private val client: OkHttpClient) {
    companion object {
        private const val URL = "https://blog.fefe.de/"
    }

    /**
     * Retrieve blog posts in current thread.
     * @return All raw blog posts.
     * @throws IOException Error during retrieval.
     */
    @Throws(IOException::class, ParseException::class)
    fun fetch(): String {
        val request = createRequest(URL)
        return get(request)
    }

    /**
     * Retrieve blog posts that contain the search phrase.
     * The search is case sensitive, meaning Fnord != fnord.
     * @return All raw blog posts that match $query (case sensitive).
     * @throws IOException Error during retrieval.
     */
    @Throws(IOException::class, ParseException::class)
    fun fetch(query: String): String {
        val request = createRequest("$URL?q=$query")
        return get(request)
    }

    private fun createRequest(url: String): Request {
        return Request.Builder().url(url).build()
    }

    private fun get(request: Request): String {
        val response = client.newCall(request).execute()
        return response.body!!.string()
    }
}
