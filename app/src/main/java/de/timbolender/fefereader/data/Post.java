package de.timbolender.fefereader.data;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A single blog post.
 */
public class Post {
    private final String id;
    private final long timestampId;
    private final boolean isRead;
    private final boolean isUpdated;
    private final String contents;
    private final long date;

    public Post(String id, long timestampId, boolean isRead, boolean isUpdated, String contents, long date) {
        checkNotNull(id);
        checkNotNull(contents);
        checkNotNull(date);

        this.id = id;
        this.timestampId = timestampId;
        this.isRead = isRead;
        this.isUpdated = isUpdated;
        this.contents = contents;
        this.date = date;
    }

    public Post(RawPost rawPost) {
        checkNotNull(rawPost);

        this.id = rawPost.getId();
        this.timestampId = rawPost.getTimestampId();
        this.isRead = false;
        this.isUpdated = false;
        this.contents = rawPost.getContents();
        this.date = rawPost.getDate();
    }

    public String getId() {
        return id;
    }

    public long getTimestampId() {
        return timestampId;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public String getContents() {
        return contents;
    }

    public long getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Post{" +
            "id='" + id + '\'' +
            ", timestampId=" + timestampId +
            ", isRead=" + isRead +
            ", isUpdated=" + isUpdated +
            ", contents='" + contents + '\'' +
            ", date='" + date + '\'' +
            '}';
    }
}
