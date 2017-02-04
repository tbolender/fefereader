package de.timbolender.fefesblogreader.db;

import java.util.List;

import de.timbolender.fefesblogreader.data.Post;

/**
 * Provides access to all posts.
 */
public interface PostReader {
    Post getNextPost();

    List<Post> getNextPosts(int num) throws DatabaseException;

    boolean hasNextPost() throws DatabaseException;

    void close();

    boolean isClosed();
}
