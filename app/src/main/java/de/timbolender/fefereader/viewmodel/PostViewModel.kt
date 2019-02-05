package de.timbolender.fefereader.viewmodel

import de.timbolender.fefereader.db.Post
import java.util.*

data class PostViewModel(
        val id: String,
        val timestampId: Long,
        val isRead: Boolean,
        val isUpdated: Boolean,
        val isBookmarked: Boolean,
        val contents: String,
        val date: Date
)

// Shorthand conversion
fun Post.toPostViewModel(): PostViewModel = PostViewModel(id, timestampId, isRead, isUpdated, isBookmarked, contents, date)
