package com.hover.runner.parser.model

import android.graphics.Color
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import com.hover.runner.parser.listeners.ParserClickListener
import com.hover.runner.transactions.TransactionStatus
import com.hover.sdk.parsers.HoverParser

data class Parser(
    val type: String?, val action_name: String?,
    val action_id: String?, val regex: String?,
    var category: String?, var created_date: String?, var sender: String? = null
) : TransactionStatus() {

    fun getStatusColor(): Int {
        return getColor(category)
    }

    companion object {

        fun listIdsToString(parserList: List<HoverParser>): String {
            val parsers = StringBuilder()
            for (hoverParser in parserList) {
                parsers.append(hoverParser.serverId).append(", ")
            }
            var parserString = ""
            if (parsers.toString().isNotEmpty()) parserString =
                parsers.toString().substring(0, parsers.length - 2)
            return parserString
        }

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

        private class MyClickableSpan(
            private val mText: String,
            private val clickListener: ParserClickListener
        ) : ClickableSpan() {
            override fun onClick(widget: View) {
                clickListener.onParserItemClicked(mText.replace(" ", "").trim { it <= ' ' })
            }
        }


    }
}