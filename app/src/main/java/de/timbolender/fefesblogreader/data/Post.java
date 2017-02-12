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
    private final String contents;
    private final String date;

    public Post(String id, long fetchedTimestamp, boolean isRead, boolean isUpdated, String contents, String date) {
        checkNotNull(id);
        checkNotNull(contents);
        checkNotNull(date);

        this.id = id;
        this.fetchedTimestamp = fetchedTimestamp;
        this.isRead = isRead;
        this.isUpdated = isUpdated;
        this.contents = contents;
        this.date = date;
    }

    public Post(RawPost rawPost) {
        checkNotNull(rawPost);

        this.id = rawPost.getId();
        this.fetchedTimestamp = rawPost.getFetchedTimestamp();
        this.isRead = false;
        this.isUpdated = false;
        this.contents = rawPost.getContents();
        this.date = rawPost.getDate();
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

    public String getContents() {
        return contents;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Post{" +
            "id='" + id + '\'' +
            ", fetchedTimestamp=" + fetchedTimestamp +
            ", isRead=" + isRead +
            ", isUpdated=" + isUpdated +
            ", contents='" + contents + '\'' +
            ", date='" + date + '\'' +
            '}';
    }
}
