package de.timbolender.fefereader.util

import android.os.Build
import android.text.Spanned

object Html {
    @Suppress("DEPRECATION")
    @JvmStatic
    fun fromHtml(htmlText: String): Spanned {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            android.text.Html.fromHtml(htmlText)
        } else {
            android.text.Html.fromHtml(htmlText, android.text.Html.FROM_HTML_MODE_COMPACT)
        }
    }
}
