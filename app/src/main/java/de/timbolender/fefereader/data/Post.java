package de.timbolender.fefereader.data;

import android.os.Parcel;
import android.os.Parcelable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A single blog post.
 */
public class Post implements Parcelable {
    private final String id;
    private final long timestampId;
    private final boolean isRead;
    private final boolean isUpdated;
    private final boolean isBookmarked;
    private final String contents;
    private final long date;

    public Post(String id, long timestampId, boolean isRead, boolean isUpdated, boolean isBookmarked, String contents, long date) {
        checkNotNull(id);
        checkNotNull(contents);
        checkNotNull(date);

        this.id = id;
        this.timestampId = timestampId;
        this.isRead = isRead;
        this.isUpdated = isUpdated;
        this.isBookmarked = isBookmarked;
        this.contents = contents;
        this.date = date;
    }

    public Post(RawPost rawPost) {
        checkNotNull(rawPost);

        this.id = rawPost.getId();
        this.timestampId = rawPost.getTimestampId();
        this.isRead = false;
        this.isUpdated = false;
        this.isBookmarked = false;
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

    public boolean isBookmarked() {
        return isBookmarked;
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
            ", isBookmarked=" + isBookmarked +
            ", contents='" + contents + '\'' +
            ", date='" + date + '\'' +
            '}';
    }

    //
    // Parcelable
    //


    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        checkNotNull(out);

        out.writeString(id);
        out.writeLong(timestampId);
        out.writeByte((byte) (isRead ? 1 : 0));
        out.writeByte((byte) (isUpdated ? 1 : 0));
        out.writeByte((byte) (isBookmarked ? 1 : 0));
        out.writeString(contents);
        out.writeLong(date);
    }

    private Post(Parcel in) {
        checkNotNull(in);

        this.id = in.readString();
        this.timestampId = in.readLong();
        this.isRead = in.readByte() == 1;
        this.isUpdated = in.readByte() == 1;
        this.isBookmarked = in.readByte() == 1;
        this.contents = in.readString();
        this.date = in.readLong();
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
