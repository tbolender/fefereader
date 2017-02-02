package de.timbolender.fefesblogreader.data;

/**
 * A single blog post.
 */
public class Post {
    private String id;
    private long fetchedTimestamp;
    private boolean isRead;
    private boolean isUpdated;
    private String content;
    private String date;

    public Post(String id, long fetchedTimestamp, boolean isRead, boolean isUpdated, String content, String date) {
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
