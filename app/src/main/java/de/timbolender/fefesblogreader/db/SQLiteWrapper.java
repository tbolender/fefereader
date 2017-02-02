package de.timbolender.fefesblogreader.db;

import android.database.sqlite.SQLiteDatabase;

import de.timbolender.fefesblogreader.data.Post;
import de.timbolender.fefesblogreader.data.RawPost;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Allows loading of posts from SQLite database.
 */
public class SQLiteWrapper implements DatabaseWrapper{
    private final SQLiteDatabase database;

    public SQLiteWrapper(SQLiteDatabase database) {
        checkNotNull(database);

        this.database = database;
    }

    @Override
    public Post addPost(RawPost rawPost) throws DatabaseException {
        return null;
    }

    @Override
    public Post updatePost(RawPost updatedPost) throws DatabaseException {
        return null;
    }

    @Override
    public Post getPost(String id) throws DatabaseException {
        return null;
    }

    @Override
    public PostReader getPostsReader() throws DatabaseException {
        return null;
    }

    @Override
    public Post markRead(Post post) throws DatabaseException {
        return null;
    }
}
