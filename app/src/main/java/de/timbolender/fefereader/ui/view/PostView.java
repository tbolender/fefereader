package de.timbolender.fefereader.ui.view;

import android.content.Context;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.util.Html;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Displays full content of a post.
 */
public class PostView extends LinearLayout {
    //
    // Constructors simply passing data forward.
    //

    public PostView(Context context) {
        this(context, null);
    }

    public PostView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Fill view with data from Post object.
     *
     * @param post Object to load data from.
     */
    public void fill(Post post) {
        checkNotNull(post);

        // Set text values
        TextView preview = ButterKnife.findById(this, R.id.contents_preview);
        Spanned previewText = Html.fromHtml(post.getContents());
        preview.setText(previewText);
    }
}
