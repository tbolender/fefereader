package de.timbolender.fefereader.db.migration

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import de.timbolender.fefereader.db.Post
import de.timbolender.fefereader.db.PostDao
import java.util.*

class ManualToRoomMigration(val context: Context, val postDao: PostDao) {
    companion object {
        private val TAG: String = ManualToRoomMigration::class.simpleName!!

        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FefesBlogDatabase.db"

        private const val TABLE_NAME = "post"
        private const val _ID = "id"
        private const val COLUMN_NAME_TIMESTAMP_ID = "timestamp_id"
        private const val COLUMN_NAME_IS_READ = "is_read"
        private const val COLUMN_NAME_IS_UPDATED = "is_updated"
        private const val COLUMN_NAME_IS_BOOKMARKED = "is_bookmarked"
        private const val COLUMN_NAME_CONTENTS = "contents"
        private const val COLUMN_NAME_DATE = "date"
    }

    private val databaseFile
        get() = context.getDatabasePath(DATABASE_NAME)

    // Test if old database exists
    val isMigrationNecessary: Boolean
        get() = databaseFile.exists()

    fun migrate() {
        Log.d(TAG, "Migrating manual database to Room")
        val oldDatabase = openOldDatabase()
        val cursor = oldDatabase.query(TABLE_NAME, null, null, null, null, null, null)

        // Migrate all post we can find
        while(cursor.moveToNext()) {
            val post = getCurrentPost(cursor)
            Log.d(TAG, "Migrate post ${post.id}")
            postDao.insertPosts(post)
        }

        cursor.close()
        databaseFile.delete()

        Log.d(TAG, "Migration complete")
    }

    private fun openOldDatabase(): SQLiteDatabase {
        val sqliteHelper = object: SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
            override fun onCreate(p0: SQLiteDatabase?) {}
            override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}
        }
        return sqliteHelper.readableDatabase!!
    }

    private fun getCurrentPost(cursor: Cursor): Post {
        val id = cursor.getString(cursor.getColumnIndexOrThrow(_ID))
        val timestampId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_TIMESTAMP_ID)).toLong()
        val isRead = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_IS_READ)) == 1
        val isUpdated = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_IS_UPDATED)) == 1
        val isBookmarked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_IS_BOOKMARKED)) == 1
        val contents = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_CONTENTS))
        val date = Date(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE)).toLong())
        return Post(id, timestampId, isRead, isUpdated, isBookmarked, contents, date)
    }
}
