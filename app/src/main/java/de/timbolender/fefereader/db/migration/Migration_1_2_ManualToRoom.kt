package de.timbolender.fefereader.db.migration

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

class Migration_1_2_ManualToRoom : Migration(1, 2) {
    companion object {
        private val TAG: String = Migration_1_2_ManualToRoom::class.simpleName!!

        private const val TABLE_NAME = "post"
        private const val _ID = "id"
        private const val COLUMN_NAME_TIMESTAMP_ID = "timestamp_id"
        private const val COLUMN_NAME_IS_READ = "is_read"
        private const val COLUMN_NAME_IS_UPDATED = "is_updated"
        private const val COLUMN_NAME_IS_BOOKMARKED = "is_bookmarked"
        private const val COLUMN_NAME_CONTENTS = "contents"
        private const val COLUMN_NAME_DATE = "date"
    }

    override fun migrate(database: SupportSQLiteDatabase) {
        Log.i(TAG, "Prepare migration 1 to 2")
        prepareMigration(database)

        Log.i(TAG, "Migrating posts")
        migratePosts(database)

        Log.i(TAG, "Finalizing")
        finalizeMigration(database)

        Log.i(TAG, "Migration complete")
    }

    private fun prepareMigration(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `post_room` " +
                "(`id` TEXT NOT NULL, `timestampId` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, `isUpdated` INTEGER NOT NULL," +
                "`isBookmarked` INTEGER NOT NULL, `contents` TEXT NOT NULL, `date` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        database.execSQL("DELETE FROM `post_room`")
        database.execSQL("CREATE  INDEX `index_Post_id` ON `post_room` (`id`)")
        database.execSQL("CREATE  INDEX `index_Post_timestampId` ON `post_room` (`timestampId`)")
    }

    private fun migratePosts(database: SupportSQLiteDatabase) {
        val cursor = database.query("select * from `${TABLE_NAME}`")

        // Migrate all post we can find
        while(cursor.moveToNext()) {
            val postValues = loadPost(cursor)
            Log.d(TAG, "Migrate post ${postValues.getAsString("id")}")
            database.insert("post_room", SQLiteDatabase.CONFLICT_ABORT, postValues)
        }

        cursor.close()
    }

    private fun loadPost(cursor: Cursor): ContentValues {
        val id = cursor.getString(cursor.getColumnIndexOrThrow(_ID))
        val timestampId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_TIMESTAMP_ID)).toLong()
        val isRead = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_IS_READ)) == 1
        val isUpdated = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_IS_UPDATED)) == 1
        val isBookmarked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_IS_BOOKMARKED)) == 1
        val contents = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_CONTENTS))
        val date = Date(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE)).toLong())

        val contentValues = ContentValues()
        contentValues.put("id", id)
        contentValues.put("timestampId", timestampId)
        contentValues.put("isRead", isRead)
        contentValues.put("isUpdated", isUpdated)
        contentValues.put("isBookmarked", isBookmarked)
        contentValues.put("contents", contents)
        contentValues.put("date", date.time)
        return contentValues
    }


    private fun finalizeMigration(database: SupportSQLiteDatabase) {
        database.execSQL("alter table post rename to post_old")
        database.execSQL("alter table post_room rename to Post")
        database.execSQL("drop table post_old")
    }
}
