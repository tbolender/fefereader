package de.timbolender.fefereader.util

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion

@BindingConversion()
fun convertVisibility(visible: Boolean): Int {
    return if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("android:html_text")
fun setHtmlText(view: TextView, value: String?) {
    view.text = Html.fromHtml(if (value != null) value else "")
}

@BindingAdapter("android:textStyle")
fun setTextStyle(view: TextView, value: String?) {
    when(value) {
        "bold" -> view.setTypeface(view.typeface, Typeface.BOLD)
        "italic" -> view.setTypeface(view.typeface, Typeface.ITALIC)
        else -> view.setTypeface(view.typeface, Typeface.NORMAL)
    }
}
