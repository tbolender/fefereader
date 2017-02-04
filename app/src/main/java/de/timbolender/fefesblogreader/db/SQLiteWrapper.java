package de.timbolender.fefesblogreader.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import de.timbolender.fefesblogreader.data.Post;
import de.timbolender.fefesblogreader.data.RawPost;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.timbolender.fefesblogreader.db.SQLiteFefesBlogContract.PostEntry;

/**
 * Allows loading of posts from SQLite database.
 */
public class SQLiteWrapper implements DatabaseWrapper {
    private final SQLiteDatabase database;

    public SQLiteWrapper(SQLiteDatabase database) {
        checkNotNull(database);

        this.database = database;
    }

    @Override
    public Post addPost(RawPost rawPost) throws DatabaseException {
        try {
            ContentValues values = new ContentValues();
            values.put(PostEntry.COLUMN_NAME_ID, rawPost.getId());
            values.put(PostEntry.COLUMN_NAME_CONTENTS, rawPost.getContents());
            values.put(PostEntry.COLUMN_NAME_DATE, rawPost.getDate());
            values.put(PostEntry.COLUMN_NAME_FETCHED_TIMESTAMP, Long.toString(rawPost.getFetchedTimestamp()));

            database.insertOrThrow(PostEntry.TABLE_NAME, null, values);

            return new Post(rawPost);
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Post updatePost(RawPost updatedPost) throws DatabaseException {
        ContentValues values = new ContentValues();
        values.put(PostEntry.COLUMN_NAME_CONTENTS, updatedPost.getContents());
        values.put(PostEntry.COLUMN_NAME_DATE, updatedPost.getDate());

        String selection = PostEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { updatedPost.getId() };

        if(database.update(PostEntry.TABLE_NAME, values, selection, selectionArgs) == 0) {
            throw new DatabaseException("No post found with given id");
        }

        return getPost(updatedPost.getId());
    }

    /**
     * Fetch post with given id
     * @param id Id of post.
     * @return The post as it is stored in the database.
     * @throws DatabaseException
     */
    @Override
    public Post getPost(String id) throws DatabaseException {
        try {
            String[] projection = {
                PostEntry.COLUMN_NAME_ID,
                PostEntry.COLUMN_NAME_FETCHED_TIMESTAMP,
                PostEntry.COLUMN_NAME_IS_READ,
                PostEntry.COLUMN_NAME_IS_UPDATED,
                PostEntry.COLUMN_NAME_CONTENTS,
                PostEntry.COLUMN_NAME_DATE
            };

            String selection = PostEntry.COLUMN_NAME_ID + " = ?";
            String[] selectionArgs = {id};

            Cursor cursor = database.query(PostEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if(!cursor.moveToNext()) {
                cursor.close();
                throw new DatabaseException("No post found with given id");
            }

            Post post = new Post(
                cursor.getString(cursor.getColumnIndexOrThrow(PostEntry.COLUMN_NAME_ID)),
                Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(PostEntry.COLUMN_NAME_FETCHED_TIMESTAMP))),
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
                PostEntry.COLUMN_NAME_ID,
                PostEntry.COLUMN_NAME_FETCHED_TIMESTAMP,
                PostEntry.COLUMN_NAME_IS_READ,
                PostEntry.COLUMN_NAME_IS_UPDATED,
                PostEntry.COLUMN_NAME_CONTENTS,
                PostEntry.COLUMN_NAME_DATE
            };

            String sortOrder = PostEntry.COLUMN_NAME_FETCHED_TIMESTAMP + " DESC";

            Cursor cursor = database.query(PostEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);

            return new SQLiteReader(cursor);
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Post markRead(Post post) throws DatabaseException {
        ContentValues values = new ContentValues();
        values.put(PostEntry.COLUMN_NAME_IS_READ, true);
        values.put(PostEntry.COLUMN_NAME_IS_UPDATED, false);

        String selection = PostEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { post.getId() };

        if(database.update(PostEntry.TABLE_NAME, values, selection, selectionArgs) == 0) {
            throw new DatabaseException("No post found with that id");
        }

        return new Post(post.getId(), post.getFetchedTimestamp(), true, false, post.getContents(), post.getDate());
    }

    @Override
    public void cleanUp() {
        database.close();
    }
}
