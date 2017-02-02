package de.timbolender.fefesblogreader.data;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A single blog post.
 */
public class Post {
    private final String id;
    private final long fetchedTimestamp;
    private final boolean isRead;
    private final boolean isUpdated;
    private final String content;
    private final String date;

    public Post(String id, long fetchedTimestamp, boolean isRead, boolean isUpdated, String content, String date) {
        checkNotNull(id);
        checkNotNull(content);
        checkNotNull(date);

        this.id = id;
        this.fetchedTimestamp = fetchedTimestamp;
        this.isRead = isRead;
        this.isUpdated = isUpdated;
        this.content = content;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public long getFetchedTimestamp() {
        return fetchedTimestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }
}
