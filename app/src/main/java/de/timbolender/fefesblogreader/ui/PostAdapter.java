package de.timbolender.fefesblogreader.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.timbolender.fefesblogreader.R;
import de.timbolender.fefesblogreader.data.Post;
import de.timbolender.fefesblogreader.db.PostReader;
import de.timbolender.fefesblogreader.ui.view.PostListItem;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables displaying of post in a RecyclerView.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private final PostReader reader;
    private final List<Post> buffer;
    private final OnPostSelectedListener listener;

    public PostAdapter(List<Post> posts, OnPostSelectedListener listener) {
        checkNotNull(posts);

        this.reader = null;
        this.buffer = ImmutableList.copyOf(posts);
        this.listener = listener;
    }
    public PostAdapter(PostReader reader, OnPostSelectedListener listener) {
        checkNotNull(reader);

        this.reader = reader;
        this.listener = listener;
        this.buffer = new ArrayList<>();
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.view_list_item_post, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder holder, int position) {
        if(position >= buffer.size()) {
            // Read enough from reader
            int missing = position - buffer.size() + 1;
            buffer.addAll(reader.getNextPosts(missing));
        }
        Post post = buffer.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return (reader != null) ? reader.getCount() : buffer.size();
    }

    /**
     * Wraps an actual entry view in the list.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.post_list_item)
        PostListItem view;

        private Post post;

        public ViewHolder(View itemView, final OnPostSelectedListener listener) {
            super(itemView);

            checkNotNull(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) listener.OnPostSelected(post);
                }
            });
        }

        public void bind(Post post) {
            this.post = post;
            view.fill(post);
        }
    }

    public interface OnPostSelectedListener {
        /**
         * Called when post entry was selected.
         * @param post Selected post.
         */
        void OnPostSelected(Post post);
    }
}
