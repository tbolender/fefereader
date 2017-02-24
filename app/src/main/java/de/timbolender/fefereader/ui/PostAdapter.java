package de.timbolender.fefereader.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.db.PostReader;
import de.timbolender.fefereader.ui.view.PostListItem;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables displaying of post in a RecyclerView.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private PostReader reader;
    private final OnPostSelectedListener listener;

    public PostAdapter(PostReader reader, OnPostSelectedListener listener) {
        checkNotNull(reader);

        this.reader = reader;
        this.listener = listener;
    }

    public void setReader(PostReader reader) {
        this.reader = reader;
        notifyDataSetChanged();
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.view_list_item_post, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder holder, int position) {
        Post post = reader.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return reader.getCount();
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
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(listener != null) listener.OnPostLongPressed(post);
                    return listener != null;
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

        /**
         * Called when post entry is long pressed.
         * @param post Selected post.
         */
        void OnPostLongPressed(Post post);
    }
}
