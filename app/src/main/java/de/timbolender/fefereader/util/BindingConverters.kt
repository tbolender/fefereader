package de.timbolender.fefereader.util

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

object BindingConverters {
    @JvmStatic
    @BindingAdapter("android:visibility")
    fun setVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("android:html_text")
    fun setHtmlText(view: TextView, value: String?) {
        view.text = Html.fromHtml(if (value != null) value else "").toString()
    }

    @JvmStatic
    @BindingAdapter("android:textStyle")
    fun setTextStyle(view: TextView, value: String?) {
        when (value) {
            "bold" -> view.setTypeface(null, Typeface.BOLD)
            "italic" -> view.setTypeface(null, Typeface.ITALIC)
            else -> view.setTypeface(null, Typeface.NORMAL)
        }
    }
}
