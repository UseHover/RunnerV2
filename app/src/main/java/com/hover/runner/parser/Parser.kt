package com.hover.runner.parser

import android.graphics.Color
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import com.hover.runner.utils.UIHelper

class Parser {

    companion object {
        fun convertTextToLinks(text: String, tv: TextView, clickListener: ParserClickListener) {
            val ss = SpannableString(text)
            val items = text.split(", ").toTypedArray()
            var start = 0
            var end: Int
            for (item in items) {
                end = start + item.length
                if (start < end) {
                    ss.setSpan(UnderlineSpan(), start, end, 0)
                    ss.setSpan(MyClickableSpan(item, clickListener), start, end, 0)
                    ss.setSpan(ForegroundColorSpan(Color.WHITE), start, end, 0)
                }
                start += item.length + 2 //comma and space in the original text ;)
            }
            tv.movementMethod = LinkMovementMethod.getInstance()
            tv.setText(ss, TextView.BufferType.SPANNABLE)
        }

        private class MyClickableSpan(private val mText: String, private val clickListener: ParserClickListener) : ClickableSpan() {
            override fun onClick(widget: View) {
                clickListener.onParserItemClicked(mText.replace(" ", "").trim { it <= ' ' })
            }
        }
    }
}