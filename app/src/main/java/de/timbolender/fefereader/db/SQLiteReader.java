package de.timbolender.fefereader.db;

import android.database.Cursor;
import android.database.SQLException;

import java.util.Objects;

import de.timbolender.fefereader.data.Post;

/**
 * Reads posts from SQLite cursor.
 */
public class SQLiteReader implements PostReader {
    private final Cursor cursor;

    public SQLiteReader(Cursor cursor) {
        this.cursor = Objects.requireNonNull(cursor);
    }

    public Post get(int index) throws DatabaseException {
        try {
            if(!cursor.moveToPosition(index)) {
                throw new DatabaseException("Could not move to position " + index + ", count is " + cursor.getCount());
            }

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry._ID));
            Long fetchedTimestamp = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_TIMESTAMP_ID)));
            boolean isRead = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_IS_READ)) == 1;
            boolean isUpdated = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_IS_UPDATED)) == 1;
            boolean isBookmarked = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_IS_BOOKMARKED)) == 1;
            String contents = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_CONTENTS));
            long date = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_DATE)));

            return new Post(id, fetchedTimestamp, isRead, isUpdated, isBookmarked, contents, date);
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public void close() {
        cursor.close();
    }

    @Override
    public boolean isClosed() {
        return cursor.isClosed();
    }
}
