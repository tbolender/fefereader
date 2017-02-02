package de.timbolender.fefesblogreader.data;

/**
 * The minimal data representing a post.
 */
public class RawPost {
    private String id;
    private long fetchedTimestamp;
    private String content;
    private String date;

    public RawPost(String id, long fetchedTimestamp, String content, String date) {
        this.id = id;
        this.fetchedTimestamp = fetchedTimestamp;
        this.content = content;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public long getFetchedTimestamp() {
        return fetchedTimestamp;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }
}
