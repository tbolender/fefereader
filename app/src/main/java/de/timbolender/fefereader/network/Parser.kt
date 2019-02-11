package de.timbolender.fefereader.network

import android.util.Log
import org.jsoup.Jsoup
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Parses a blog page for posts.
 */
class Parser {
    companion object {
        private val TAG: String = Parser::class.simpleName!!

        private val DATE_FORMAT = SimpleDateFormat("EEE MMM d yyyy", Locale.US)
    }

    /**
     * Parse blog posts from given text.
     * @param content Text to parse.
     * @return All parsed blog posts.
     * @throws ParseException Error when parsing.
     */
    @Throws(ParseException::class)
    fun parse(content: String): List<RawPost> {
        val document = Jsoup.parse(content)

        var currentDate: Date? = null
        val postList = ArrayList<RawPost>()

        val baseTime = System.currentTimeMillis() * 1000
        var indexCounter: Long = 999

        for (element in document.select("body > h3, body > ul")) {
            if (element.tagName() == "h3") {
                currentDate = DATE_FORMAT.parse(element.text())
            } else {
                if (currentDate == null) {
                    throw ParseException("No date available for first post!", -1)
                }

                for (entry in element.children().select("li")) {
                    // Skip non-direct children
                    if (entry.parent() != element)
                        continue

                    val entryContent = entry.children()
                    val idElement = entryContent[0]
                    val id = idElement.attr("href").replace("?ts=", "")
                    val postContent = entry.html().replace(idElement.outerHtml(), "")
                    val post = RawPost(id, baseTime + indexCounter, postContent, currentDate.time)
                    Log.v(TAG, "Parsed $post")
                    postList.add(post)

                    // Decrement index counter
                    if (indexCounter == 0L) {
                        throw IllegalArgumentException("Parser supports only 999 posts per parsing")
                    }

                    indexCounter--
                }
            }
        }

        return Collections.unmodifiableList(postList)
    }
}
