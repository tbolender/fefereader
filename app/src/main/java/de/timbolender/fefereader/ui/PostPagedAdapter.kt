package de.timbolender.fefereader.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.timbolender.fefereader.R
import de.timbolender.fefereader.databinding.ViewItemPostBinding
import de.timbolender.fefereader.db.Post
import de.timbolender.fefereader.viewmodel.toPostViewModel

/**
 * Enables paged displaying of posts in a RecyclerView.
 */
class PostPagedAdapter(private val listener: OnPostSelectedListener):
        PagedListAdapter<Post, PostPagedAdapter.ViewHolder>(DIFF_HANDLER) {

    companion object {
        private val TAG: String = PostPagedAdapter::class.simpleName!!

        private val DIFF_HANDLER = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostPagedAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewItemPostBinding>(inflater, R.layout.view_item_post, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostPagedAdapter.ViewHolder, position: Int) {
        val post = getItem(position)!!
        holder.bindTo(post, listener)
    }

    /**
     * Wraps an actual entry view in the list.
     */
    inner class ViewHolder(private val binding: ViewItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo(post: Post, listener: OnPostSelectedListener) {
            Log.d(TAG, "Binding post ${post.id} to item")
            binding.post = post.toPostViewModel()
            binding.listener = listener
            binding.executePendingBindings()
        }
    }

    interface OnPostSelectedListener {
        /**
         * Called when post entry was selected.
         * @param postId Selected post id.
         */
        fun onPostSelected(postId: String)

        /**
         * Called when post entry is long pressed.
         * @param postId Selected post id.
         * @return True if the event is consumed.
         */
        fun onPostLongPressed(postId: String): Boolean
    }
}
