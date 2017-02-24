package de.timbolender.fefereader.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.util.Html;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A view showing a post content preview.
 */
public class PostListItem extends LinearLayout {
    //
    // Constructors simply passing data forward.
    //

    public PostListItem(Context context) {
        this(context, null);
    }

    public PostListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Fill list item with data from Post object.
     *
     * @param post Object to load data from.
     */
    public void fill(Post post) {
        checkNotNull(post);

        // Set text values
        TextView preview = ButterKnife.findById(this, R.id.contents_preview);
        String previewText = Html.fromHtml(post.getContents()).toString();
        preview.setText(previewText);
        preview.setTypeface((post.isRead() && !post.isUpdated()) ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);

        View bookmarkView = ButterKnife.findById(this, R.id.bookmark_image);
        bookmarkView.setVisibility(post.isBookmarked() ? VISIBLE : GONE);

        // Attach dispenser to view
        setTag(post);
    }
}
