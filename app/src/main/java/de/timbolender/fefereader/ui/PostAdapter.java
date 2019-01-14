package de.timbolender.fefereader.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.databinding.ViewListItemPostBinding;
import de.timbolender.fefereader.db.PostReader;
import de.timbolender.fefereader.viewmodel.PostViewModel;

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

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewListItemPostBinding binding = DataBindingUtil.inflate(inflater, R.layout.view_list_item_post, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = reader.get(position);
        PostViewModel vm = new PostViewModel(
            post.getId(), post.getTimestampId(), post.isRead(), post.isUpdated(),
            post.isBookmarked(), post.getContents(), post.getDate()
        );
        holder.bindTo(vm);
    }

    @Override
    public int getItemCount() {
        return reader.getCount();
    }

    /**
     * Wraps an actual entry view in the list.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewListItemPostBinding binding;

        public ViewHolder(ViewListItemPostBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bindTo(PostViewModel post) {
            this.binding.setPost(post);
            this.binding.setListener(listener);
        }
    }

    public interface OnPostSelectedListener {
        /**
         * Called when post entry was selected.
         * @param postId Selected post id.
         */
        void OnPostSelected(String postId);

        /**
         * Called when post entry is long pressed.
         * @param postId Selected post id.
         * @return True if the event is consumed.
         */
        boolean OnPostLongPressed(String postId);
    }
}
