package de.timbolender.fefereader.data

/**
 * The minimal data representing a post.
 */
data class RawPost(val id: String,
                   val timestampId: Long,
                   val contents: String,
                   val date: Long)
