package de.timbolender.fefesblogreader.db;

import java.util.List;

import de.timbolender.fefesblogreader.data.Post;

/**
 * Provides access to all posts.
 */
public interface PostReader {
    Post getNextPost();

    List<Post> getNextPosts(int num);

    boolean hasNextPost();

    boolean isClosed();
}
