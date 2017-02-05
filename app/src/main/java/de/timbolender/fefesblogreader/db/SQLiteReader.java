package de.timbolender.fefesblogreader.db;

import android.database.Cursor;
import android.database.SQLException;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.timbolender.fefesblogreader.data.Post;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads posts from SQLite cursor.
 */
public class SQLiteReader implements PostReader {
    private final Cursor cursor;

    public SQLiteReader(Cursor cursor) {
        checkNotNull(cursor);

        this.cursor = cursor;
    }

    @Override
    public Post getNextPost() throws DatabaseException {
        try {
            cursor.moveToNext();

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_ID));
            Long fetchedTimestamp = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_FETCHED_TIMESTAMP)));
            boolean isRead = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_IS_READ)) == 1;
            boolean isUpdated = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_IS_UPDATED)) == 1;
            String contents = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_CONTENTS));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteFefesBlogContract.PostEntry.COLUMN_NAME_DATE));

            return new Post(id, fetchedTimestamp, isRead, isUpdated, contents, date);
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<Post> getNextPosts(int num) throws DatabaseException {
        ImmutableList.Builder<Post> builder = new ImmutableList.Builder<>();
        for(int i = 0; i < num && hasNextPost(); i++) {
            builder.add(getNextPost());
        }
        return builder.build();
    }

    @Override
    public boolean hasNextPost() {
        return !cursor.isAfterLast();
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
