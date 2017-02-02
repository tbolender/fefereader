package de.timbolender.fefesblogreader.data;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The minimal data representing a post.
 */
public class RawPost {
    private final String id;
    private final long fetchedTimestamp;
    private final String content;
    private final String date;

    public RawPost(String id, long fetchedTimestamp, String content, String date) {
        checkNotNull(id);
        checkNotNull(content);
        checkNotNull(date);

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
