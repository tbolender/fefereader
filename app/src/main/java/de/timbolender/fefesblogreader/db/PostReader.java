package de.timbolender.fefesblogreader.db;

import java.util.List;

import de.timbolender.fefesblogreader.data.Post;

/**
 * Provides access to all posts.
 */
public interface PostReader {
    /**
     * @return Next available post.
     * @throws IllegalStateException Thrown if no post is available or error during read occurred.
     */
    Post getNextPost();

    /**
     * @param num Maximum number of posts to retrieve.
     * @return All next posts, limited to given count.
     * @throws DatabaseException Thrown if error during read occurred.
     */
    List<Post> getNextPosts(int num) throws DatabaseException;

    /**
     * @return True if next posts exists.
     * @throws DatabaseException Thrown if error during read occurred.
     */
    boolean hasNextPost() throws DatabaseException;

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
