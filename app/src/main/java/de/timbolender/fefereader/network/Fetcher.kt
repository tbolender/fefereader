package de.timbolender.fefereader.network

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.ParseException

/**
 * Fetched current front page of Fefe's blog.
 */
class Fetcher(private val client: OkHttpClient, private val parser: Parser) {
    companion object {
        private const val URL = "https://blog.fefe.de/"
    }

    /**
     * Retrieve blog posts in current thread.
     * @return All parsed blog posts.
     * @throws IOException Error during retrieval.
     * @throws ParseException Error when parsing the posts.
     */
    @Throws(IOException::class, ParseException::class)
    fun fetch(): List<RawPost> {
        val request = Request.Builder()
            .url(URL)
            .build()

        val response = client.newCall(request).execute()
        return parser.parse(response.body!!.string())
    }
}
