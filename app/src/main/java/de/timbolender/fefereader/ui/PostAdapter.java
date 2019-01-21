package de.timbolender.fefereader.ui;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        return new ViewHolder();
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return reader.getCount();
    }

    /**
     * Wraps an actual entry view in the list.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder() {
            super(null);
        }

        public void bindTo(PostViewModel post) {
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
