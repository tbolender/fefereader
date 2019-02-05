package de.timbolender.fefereader.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.util.Objects;

import androidx.annotation.RequiresApi;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.viewmodel.PostViewModel;

/**
 * Displays full content of a post.
 */
public class PostView extends LinearLayout {
    private OnLinkClickedListener listener;

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
    public void fill(PostViewModel post, String cssStyle) {
        Objects.requireNonNull(post);

        // Set up link interception
        WebView view = findViewById(R.id.contents);
        view.setBackgroundColor(Color.TRANSPARENT);
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
        String fullContent = "<html><style>" + cssStyle + "</style><body>" + post.getContents() + "</body></html>";
        // Otherwise umlaute are not displayed correctly; http://stackoverflow.com/questions/3961589/android-webview-and-loaddata
        view.loadDataWithBaseURL("https://blog.fefe.de/", fullContent, "text/html; charset=UTF-8", null, null);
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
