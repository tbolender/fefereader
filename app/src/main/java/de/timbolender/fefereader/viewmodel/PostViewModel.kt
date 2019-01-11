package de.timbolender.fefereader.viewmodel

data class PostViewModel(
        val id: String,
        val timestampId: Long,
        val isRead: Boolean,
        val isUpdated: Boolean,
        val isBookmarked: Boolean,
        val contents: String,
        val date: Long
)
