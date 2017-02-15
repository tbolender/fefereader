package de.timbolender.fefereader.db;

import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.data.RawPost;

/**
 * Wrapper interface around a data source.
 */
public interface DatabaseWrapper {
    /**
     * Add new post to database or update existing.
     * @param rawPost New post to ad.
     * @throws DatabaseException Thrown on database error.
     */
    void addOrUpdatePost(RawPost rawPost) throws DatabaseException;

    /**
     * Fetch post with given id.
     * @param id Id of post.
     * @return The post as it is stored in the database.
     * @throws DatabaseException
     */
    Post getPost(String id) throws DatabaseException;

    /**
     * @return Number of posts which are unread. Does not include updated ones.
     */
    long getUnreadPostCount();

    /**
     * @return Number of posts which are updated and not yet read.
     */
    long getUpdatedPostCount();

    /**
     * @return Reader providing access to all posts.
     * @throws DatabaseException Thrown on database error.
     */
    PostReader getPostsReader() throws DatabaseException;

    /**
     * Mark a post as read. Also removed the updated flag.
     * @param post Post to mark as read.
     * @throws DatabaseException Thrown on database error.
     */
    void markRead(Post post) throws DatabaseException;

    /**
     * Clear everything up.
     */
    void cleanUp();
}
