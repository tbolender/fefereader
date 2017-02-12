package de.timbolender.fefesblogreader.data;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The minimal data representing a post.
 */
public class RawPost {
    private final String id;
    private final long fetchedTimestamp;
    private final String contents;
    private final String date;

    public RawPost(String id, long fetchedTimestamp, String contents, String date) {
        checkNotNull(id);
        checkNotNull(contents);
        checkNotNull(date);

        this.id = id;
        this.fetchedTimestamp = fetchedTimestamp;
        this.contents = contents;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public long getFetchedTimestamp() {
        return fetchedTimestamp;
    }

    public String getContents() {
        return contents;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "RawPost{" +
            "id='" + id + '\'' +
            ", fetchedTimestamp=" + fetchedTimestamp +
            ", contents='" + contents + '\'' +
            ", date='" + date + '\'' +
            '}';
    }
}
