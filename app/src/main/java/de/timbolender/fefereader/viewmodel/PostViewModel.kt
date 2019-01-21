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
) {
    constructor(post: Post) :
            this(post.id, post.timestampId, post.isRead, post.isUpdated, post.isBookmarked, post.contents, post.date)
}
