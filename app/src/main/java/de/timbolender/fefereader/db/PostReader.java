package de.timbolender.fefereader.db;

import de.timbolender.fefereader.data.Post;

/**
 * Provides access to all posts.
 */
public interface PostReader {
    /**
     * @return Post at given index.
     * @throws IllegalStateException Thrown if post is not available or error during read occurred.
     */
    Post get(int index);

    /**
     * @return Number of posts the reader has.
     */
    int getCount();

    /**
     * Close reader. After this operation no other readings can be performed.
     */
    void close();

    /**
     * @return True if reader is closed.
     */
    boolean isClosed();
}
