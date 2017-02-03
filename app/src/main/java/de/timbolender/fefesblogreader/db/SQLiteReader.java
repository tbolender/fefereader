package de.timbolender.fefesblogreader.db;

import android.database.Cursor;

import java.util.List;

import de.timbolender.fefesblogreader.data.Post;

/**
 * Reads posts from SQLite cursor.
 */
public class SQLiteReader implements PostReader {
    private final Cursor cursor;

    public SQLiteReader(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public Post getNextPost() {
        return null;
    }

    @Override
    public List<Post> getNextPosts(int num) {
        return null;
    }

    @Override
    public boolean hasNextPost() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return false;
    }
}
