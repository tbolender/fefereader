package de.timbolender.fefesblogreader.data;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The minimal data representing a post.
 */
public class RawPost {
    private final String id;
    private final long timestampId;
    private final String contents;
    private final String date;

    public RawPost(String id, long timestampId, String contents, String date) {
        checkNotNull(id);
        checkNotNull(contents);
        checkNotNull(date);

        this.id = id;
        this.timestampId = timestampId;
        this.contents = contents;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public long getTimestampId() {
        return timestampId;
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
            ", timestampId=" + timestampId +
            ", contents='" + contents + '\'' +
            ", date='" + date + '\'' +
            '}';
    }
}
