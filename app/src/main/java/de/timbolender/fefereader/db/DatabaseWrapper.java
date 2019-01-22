package de.timbolender.fefereader.db;

/**
 * Wrapper interface around a data source.
 */
public interface DatabaseWrapper {
    int FILTER_NONE = 1;
    int FILTER_BOOKMARKED = 1 << 1;
    int FILTER_UNREAD = 1 << 2;

    /**
     * @return Number of posts which are unread.
     */
    long getUnreadPostCount();

    /**
     * @return Number of posts which are updated and were already read in the past.
     */
    long getUpdatedPostCount();
}
