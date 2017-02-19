package de.timbolender.fefereader.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Displays full content of a post.
 */
public class PostView extends LinearLayout {
    private String BASE_STYLE;

    private OnLinkClickedListener listener;

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
        int DEFAULT_MARGIN = getResources().getDimensionPixelSize(R.dimen.post_view_margin);
        //noinspection ResourceType
        String LINK_COLOR = getResources().getString(R.color.colorAccent).replace("#ff", "#");

        BASE_STYLE =
            "body { margin: " + DEFAULT_MARGIN + "px; }\n" +
            "a { color: " + LINK_COLOR + "; } ";
    }

    /**
     * Set listener for link interception.
     * @param listener Listener to use.
     */
    public void setOnLinkClickedListener(OnLinkClickedListener listener) {
        this.listener = listener;
    }

    /**
     * Fill view with data from Post object.
     *
     * @param post Object to load data from.
     */
    public void fill(Post post, String cssStyle) {
        checkNotNull(post);

        // Set up link interception
        WebView view = ButterKnife.findById(this, R.id.contents);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            view.setWebViewClient(new LinkClickInterceptor() {
                @SuppressWarnings("deprecation")
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    handleLink(url);
                    return true;
                }
            });
        }
        else {
            view.setWebViewClient(new LinkClickInterceptor() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    handleLink(request.getUrl().toString());
                    return true;
                }
            });
        }

        // Set content
        String fullStyle = BASE_STYLE + cssStyle;
        String fullContent = "<html><style>" + fullStyle + "</style><body>" + post.getContents() + "</body></html>";
        // Otherwise umlaute are not displayed correctly; http://stackoverflow.com/questions/3961589/android-webview-and-loaddata
        view.loadData(fullContent, "text/html; charset=UTF-8", null);
    }

    /**
     * Base class for intercepting clicks on links.
     */
    private class LinkClickInterceptor extends WebViewClient {
        void handleLink(String url) {
            if(listener != null) {
                listener.onLinkClicked(url);
            }
        }
    }

    /**
     * Provides callback to get notified if user presses on a link.
     */
    public interface OnLinkClickedListener {
        void onLinkClicked(String url);
    }
}
