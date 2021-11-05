package com.hover.runner.utils

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hover.runner.R
import com.hover.runner.actions.ActionStatusEnum
import timber.log.Timber

class UIHelper {
    companion object {
        private val INITIAL_ITEMS_FETCH = 30

        fun setMainLinearManagers(context: Context?): LinearLayoutManager? {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.initialPrefetchItemCount = INITIAL_ITEMS_FETCH
            linearLayoutManager.isSmoothScrollbarEnabled = true
            return linearLayoutManager
        }

        fun underlineText(textView: TextView, cs: String?) {
            val content = SpannableString(cs)
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            content.setSpan(Typeface.BOLD, 0, content.length, 0)
            try {
                textView.text = content
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        fun removeTextUnderline(textView: TextView) {
            val ss = SpannableString(textView.text)
            val spans = ss.getSpans(
                0, textView.text.length,
                UnderlineSpan::class.java
            )
            for (span in spans) {
                ss.removeSpan(span)
            }
        }

        fun changeStatusBarColor(activity: Activity, color: Int) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = color
        }


        fun flashMessage(context: Context, view: View?, message: String?) {
            if (view == null) flashMessage(context, message) else Snackbar.make(
                view,
                message!!,
                Snackbar.LENGTH_LONG
            ).show()
        }

        fun flashMessage(context: Context, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun getActionIconDrawable(enum: ActionStatusEnum?): Int {
            return when (enum) {
                ActionStatusEnum.PENDING -> R.drawable.ic_warning_yellow_24dp
                ActionStatusEnum.UNSUCCESSFUL -> R.drawable.ic_error_red_24dp
                else -> R.drawable.ic_check_circle_green_24dp
            }
        }
    }

}