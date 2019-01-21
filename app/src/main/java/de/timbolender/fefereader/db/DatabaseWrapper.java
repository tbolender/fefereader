package de.timbolender.fefereader.db;

import de.timbolender.fefereader.data.Post;

/**
 * Wrapper interface around a data source.
 */
public interface DatabaseWrapper {
    int FILTER_NONE = 1;
    int FILTER_BOOKMARKED = 1 << 1;
    int FILTER_UNREAD = 1 << 2;

    /**
     * Fetch post with given id.
     * @param id Id of post.
     * @return The post as it is stored in the database.
     * @throws DatabaseException
     */
    Post getPost(String id) throws DatabaseException;

    /**
     * @return Number of posts which are unread.
     */
    long getUnreadPostCount();

    /**
     * @return Number of posts which are updated and were already read in the past.
     */
    long getUpdatedPostCount();

    /**
     * @return Reader providing access to all posts.
     * @param filter Filter expression which posts to return.
     * @throws DatabaseException Thrown on database error.
     */
    PostReader getPostsReader(int filter) throws DatabaseException;

    /**
     * Save whether post is marked as read. Removes the updated flag (independent of parameter).
     * @param id Post to mark as read.
     * @param isRead True if post was read.
     * @throws DatabaseException Thrown on database error.
     */
    void setRead(String id, boolean isRead) throws DatabaseException;

    /**
     * Save whether post is saved for later.
     * @param id Id of post.
     * @param isBookmarked True if post should be saved as bookmark.
     * @throws DatabaseException Thrown on database error.
     */
    void setBookmarked(String id, boolean isBookmarked) throws DatabaseException;

    /**
     * Clear everything up.
     */
    void cleanUp();
}
