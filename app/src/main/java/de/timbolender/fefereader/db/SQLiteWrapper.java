package de.timbolender.fefereader.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.data.RawPost;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.timbolender.fefereader.db.SQLiteFefesBlogContract.PostEntry;

/**
 * Allows loading of posts from SQLite database.
 */
public class SQLiteWrapper implements DatabaseWrapper {
    private static final String TAG = SQLiteWrapper.class.getSimpleName();

    private final SQLiteDatabase database;

    public SQLiteWrapper(SQLiteDatabase database) {
        checkNotNull(database);

        this.database = database;
    }

    @Override
    public DatabaseOperation addOrUpdatePost(RawPost rawPost) throws DatabaseException {
        checkNotNull(rawPost);

        ContentValues insertValues = new ContentValues();
        insertValues.put(PostEntry._ID, rawPost.getId());
        insertValues.put(PostEntry.COLUMN_NAME_CONTENTS, rawPost.getContents());
        insertValues.put(PostEntry.COLUMN_NAME_DATE, rawPost.getDate());
        insertValues.put(PostEntry.COLUMN_NAME_TIMESTAMP_ID, Long.toString(rawPost.getTimestampId()));

        boolean success = database.insertWithOnConflict(PostEntry.TABLE_NAME, null, insertValues, SQLiteDatabase.CONFLICT_IGNORE) != -1;

        if(!success) {
            ContentValues updateValues = new ContentValues();
            updateValues.put(PostEntry.COLUMN_NAME_CONTENTS, rawPost.getContents());
            updateValues.put(PostEntry.COLUMN_NAME_DATE, rawPost.getDate());
            updateValues.put(PostEntry.COLUMN_NAME_IS_UPDATED, true);

            String selection = PostEntry._ID + " = ? AND " + PostEntry.COLUMN_NAME_CONTENTS + " <> ?";
            String[] selectionArgs = { rawPost.getId(), rawPost.getContents() };

            if(database.update(PostEntry.TABLE_NAME, updateValues, selection, selectionArgs) == 1) {
                Log.v(TAG, "Updated post " + rawPost.getId());
                return DatabaseOperation.UPDATED;
            }
            else {
                Log.v(TAG, "No update necessary for post " + rawPost.getId());
                return DatabaseOperation.NONE;
            }
        }
        else {
            Log.v(TAG, "Inserted new post " + rawPost.getId());
            return DatabaseOperation.CREATED;
        }
    }

    @Override
    public Post getPost(String id) throws DatabaseException {
        try {
            checkNotNull(id);

            String[] projection = {
                PostEntry._ID,
                PostEntry.COLUMN_NAME_TIMESTAMP_ID,
                PostEntry.COLUMN_NAME_IS_READ,
                PostEntry.COLUMN_NAME_IS_UPDATED,
                PostEntry.COLUMN_NAME_CONTENTS,
                PostEntry.COLUMN_NAME_DATE
            };

            String selection = PostEntry._ID + " = ?";
            String[] selectionArgs = {id};

            Cursor cursor = database.query(PostEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if(!cursor.moveToNext()) {
                cursor.close();
                throw new DatabaseException("No post found with id " + id);
            }

            Post post = new Post(
                cursor.getString(cursor.getColumnIndexOrThrow(PostEntry._ID)),
                Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(PostEntry.COLUMN_NAME_TIMESTAMP_ID))),
                cursor.getInt(cursor.getColumnIndexOrThrow(PostEntry.COLUMN_NAME_IS_READ)) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow(PostEntry.COLUMN_NAME_IS_UPDATED)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(PostEntry.COLUMN_NAME_CONTENTS)),
                cursor.getString(cursor.getColumnIndexOrThrow(PostEntry.COLUMN_NAME_DATE))
            );

            cursor.close();

            return post;
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public PostReader getPostsReader() throws DatabaseException {
        try {
            String[] projection = {
                PostEntry._ID,
                PostEntry.COLUMN_NAME_TIMESTAMP_ID,
                PostEntry.COLUMN_NAME_IS_READ,
                PostEntry.COLUMN_NAME_IS_UPDATED,
                PostEntry.COLUMN_NAME_CONTENTS,
                PostEntry.COLUMN_NAME_DATE
            };

            String sortOrder = PostEntry.COLUMN_NAME_TIMESTAMP_ID + " DESC";

            @SuppressLint("Recycle")
            Cursor cursor = database.query(PostEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);

            return new SQLiteReader(cursor);
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void markRead(Post post) throws DatabaseException {
        checkNotNull(post);

        ContentValues values = new ContentValues();
        values.put(PostEntry.COLUMN_NAME_IS_READ, true);
        values.put(PostEntry.COLUMN_NAME_IS_UPDATED, false);

        String selection = PostEntry._ID + " = ?";
        String[] selectionArgs = { post.getId() };

        if(database.update(PostEntry.TABLE_NAME, values, selection, selectionArgs) == 0) {
            throw new DatabaseException("No post found with id " + post.getId());
        }
    }

    @Override
    public void cleanUp() {
        database.close();
    }
}
