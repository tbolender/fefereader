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

        private const val MAX_POSTS = 9999
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

        var dateOfCurrentSection: Date? = null
        val postList = ArrayList<RawPost>()

        val baseTime = System.currentTimeMillis() * 1000
        var postIndexCounter = MAX_POSTS

        for (element in document.select("body > h3, body > ul")) {
            if (element.tagName() == "h3") {
                dateOfCurrentSection = DATE_FORMAT.parse(element.text())
            } else {
                if (dateOfCurrentSection == null) {
                    throw ParseException("No date available for first post!", -1)
                }

                for (entry in element.children().select("li")) {
                    // Skip non-direct children
                    if (entry.parent() != element)
                        continue

                    val idElement = entry.children()[0]
                    postList.add(RawPost(
                        id = idElement.attr("href").replace("?ts=", ""),
                        timestampId = baseTime + postIndexCounter,
                        contents = entry.html().replace(idElement.outerHtml(), ""),
                        date = dateOfCurrentSection.time)
                    )
                    Log.v(TAG, "Parsed ${postList.last()}")

                    // Decrement index counter
                    if (postIndexCounter == 0) {
                        throw IllegalArgumentException("Parser supports only $MAX_POSTS posts per parsing")
                    }

                    postIndexCounter--
                }
            }
        }

        return Collections.unmodifiableList(postList)
    }
}
