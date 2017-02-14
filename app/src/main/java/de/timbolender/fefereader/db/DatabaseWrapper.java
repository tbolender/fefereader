package de.timbolender.fefereader.db;

import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.data.RawPost;

/**
 * Wrapper interface around a data source.
 */
public interface DatabaseWrapper {
    /**
     * Possible operation with could be performed when adding an post to database.
     */
    enum DatabaseOperation {
        CREATED,
        UPDATED,
        NONE
    }

    /**
     * Add new post to database or update existing.
     * @param rawPost New post to ad.
     * @return The operation performed.
     * @throws DatabaseException Thrown on database error.
     */
    DatabaseOperation addOrUpdatePost(RawPost rawPost) throws DatabaseException;

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
     * @param post Post to mark as read.
     * @throws DatabaseException Thrown on database error.
     */
    void markRead(Post post) throws DatabaseException;

    /**
     * Clear everything up.
     */
    void cleanUp();
}
