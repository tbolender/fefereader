package de.timbolender.fefesblogreader.util;

import android.os.Build;
import android.text.Spanned;

/**
 * Wrapper around Android Html class for parsing.
 */
public class Html {
    public static Spanned fromHtml(String htmlText) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //noinspection deprecation
            return android.text.Html.fromHtml(htmlText);
        }
        else {
            return android.text.Html.fromHtml(htmlText, android.text.Html.FROM_HTML_MODE_COMPACT);
        }
    }
}
