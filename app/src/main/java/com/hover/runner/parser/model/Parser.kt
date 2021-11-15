package com.hover.runner.parser.model

import android.graphics.Color
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import com.hover.runner.R
import com.hover.runner.actions.ActionStatusEnum
import com.hover.runner.parser.listeners.ParserClickListener
import com.hover.sdk.parsers.HoverParser
import java.lang.StringBuilder

data class Parser(
    val type: String, val action_name: String,
    val action_id: String, val regex: String,
    val actionStatusEnum: ActionStatusEnum,
    var category: String? = null, var created_date: String, var sender: String? = null) {

    fun getStatusText() : Int {
        return when(actionStatusEnum) {
            ActionStatusEnum.SUCCEEDED ->  R.string.success_label
            ActionStatusEnum.PENDING -> R.string.pending_label
            ActionStatusEnum.FAILED -> R.string.failed_label
            else -> R.string.not_yet_run
        }
    }

    fun getStatusColor() : Int{
        return when(actionStatusEnum) {
            ActionStatusEnum.PENDING -> R.color.colorYellow
            ActionStatusEnum.FAILED -> R.color.colorRed
            ActionStatusEnum.SUCCEEDED -> R.color.colorGreen
            else -> R.color.colorPrimaryDark
        }
    }

    companion object {

        fun listIdsToString(parserList: List<HoverParser>) : String{
            val parsers = StringBuilder()
            for (hoverParser in parserList) {
                parsers.append(hoverParser.serverId).append(", ")
            }
            var parserString = ""
            if (parsers.toString().isNotEmpty()) parserString =  parsers.toString().substring(0, parsers.length - 2)
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

        private class MyClickableSpan(private val mText: String, private val clickListener: ParserClickListener) : ClickableSpan() {
            override fun onClick(widget: View) {
                clickListener.onParserItemClicked(mText.replace(" ", "").trim { it <= ' ' })
            }
        }


    }
}