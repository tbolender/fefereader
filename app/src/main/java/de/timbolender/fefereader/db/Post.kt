package de.timbolender.fefereader.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

/**
 * A single blog post.
 */
@Entity(indices = [Index("id"), Index("timestampId")])
data class Post(
        @PrimaryKey
        var id: String,
        val timestampId: Long,
        val isRead: Boolean,
        val isUpdated: Boolean,
        val isBookmarked: Boolean,
        val contents: String,
        val date: Date
)
