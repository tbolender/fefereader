package de.timbolender.fefesblogreader.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import de.timbolender.fefesblogreader.R;
import de.timbolender.fefesblogreader.data.Post;

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
        String previewText;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //noinspection deprecation
            previewText = Html.fromHtml(post.getContents()).toString();
        }
        else {
            previewText = Html.fromHtml(post.getContents(), Html.FROM_HTML_MODE_COMPACT).toString();
        }
        preview.setText(previewText);
        preview.setTypeface((post.isRead() && !post.isUpdated()) ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);

        // Attach dispenser to view
        setTag(post);
    }
}
