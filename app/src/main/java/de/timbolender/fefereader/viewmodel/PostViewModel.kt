package de.timbolender.fefereader.viewmodel

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
