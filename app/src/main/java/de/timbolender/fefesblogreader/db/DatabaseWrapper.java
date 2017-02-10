package de.timbolender.fefesblogreader.db;

import de.timbolender.fefesblogreader.data.Post;
import de.timbolender.fefesblogreader.data.RawPost;

/**
 * Wrapper interface around a data source.
 */
public interface DatabaseWrapper {
    /**
     * Add new post to database or update existing.
     * @param rawPost New post to ad.
     * @return The post as it is stored in the database.
     * @throws DatabaseException Thrown on database error.
     */
    Post addOrUpdatePost(RawPost rawPost) throws DatabaseException;

    /**
     * Fetch post with given id.
     * @param id Id of post.
     * @return The post as it is stored in the database.
     * @throws DatabaseException
     */
    Post getPost(String id) throws DatabaseException;

    /**
     * @return Reader providing access to all posts.
     * @throws DatabaseException Thrown on database error.
     */
    PostReader getPostsReader() throws DatabaseException;

    /**
     * Mark a post as read. Also removed the updated flag.
     * @param post Post to mar as read.
     * @return The post as it is stored in the database.
     * @throws DatabaseException Thrown on database error.
     */
    Post markRead(Post post) throws DatabaseException;

    /**
     * Clear everything up.
     */
    void cleanUp();
}
