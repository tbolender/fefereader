package de.timbolender.fefereader.data

/**
 * A single blog post.
 */
data class Post(
        val id: String,
        val timestampId: Long,
        val isRead: Boolean,
        val isUpdated: Boolean,
        val isBookmarked: Boolean,
        val contents: String,
        val date: Long
);
