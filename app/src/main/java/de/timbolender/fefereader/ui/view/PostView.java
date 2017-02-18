package de.timbolender.fefereader.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Displays full content of a post.
 */
public class PostView extends LinearLayout {
    private int DEFAULT_MARGIN;
    private String LINK_COLOR;
    private String BASE_STYLE;

    //
    // Constructors simply passing data forward.
    //

    public PostView(Context context) {
        this(context, null);

        init();
    }

    public PostView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        DEFAULT_MARGIN = getResources().getDimensionPixelSize(R.dimen.post_view_margin);
        //noinspection ResourceType
        LINK_COLOR = getResources().getString(R.color.colorAccent).replace("#ff", "#");

        BASE_STYLE =
            "body { margin: " + DEFAULT_MARGIN + "px; }\n" +
            "a { color: " + LINK_COLOR + "; } ";
    }

    /**
     * Fill view with data from Post object.
     *
     * @param post Object to load data from.
     */
    public void fill(Post post, String cssStyle) {
        checkNotNull(post);

        // Set text values
        WebView view = ButterKnife.findById(this, R.id.contents);
        String fullStyle = BASE_STYLE + cssStyle;
        String fullContent = "<html><style>" + fullStyle + "</style><body>" + post.getContents() + "</body></html>";
        // Otherwise umlaute are not displayed correctly; http://stackoverflow.com/questions/3961589/android-webview-and-loaddata
        view.loadData(fullContent, "text/html; charset=UTF-8", null);
    }
}
