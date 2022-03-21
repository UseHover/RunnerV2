package com.hover.runner.utils

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView
import com.hover.runner.R
import timber.log.Timber

class TextViewUtils {
	companion object {

		fun TextView.activateView() {
			this.setTextColor(RunnerColor(this.context).WHITE)
			this.underline()
			this.isClickable = true
		}

		fun TextView.deactivateView() {
			this.setTextColor(RunnerColor(this.context).GRAY)
			this.removeUnderline()
			this.isClickable = false
		}

		fun TextView.underline() {
			underlineText(this, this.text.toString())
		}

		fun TextView.underline(newValue: String?) {
			newValue?.let { underlineText(this, it) }
		}

		fun TextView.removeUnderline() {
			val ss = SpannableString(this.text)
			val spans = ss.getSpans(0, this.text.length, UnderlineSpan::class.java)
			for (span in spans) {
				ss.removeSpan(span)
			}
		}

		private fun underlineText(textView: TextView, cs: String?) {
			val content = SpannableString(cs)
			content.setSpan(UnderlineSpan(), 0, content.length, 0)
			content.setSpan(Typeface.BOLD, 0, content.length, 0)
			try {
				textView.text = content
			} catch (e: Exception) {
				Timber.e(e)
			}
		}

		fun TextView.styleAsFilterOn() {
			setTextColor(resources.getColor(R.color.runnerPrimary))
			setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_purple_24dp, 0, 0, 0)
			compoundDrawablePadding = 8
		}

		fun TextView.styleAsFilterOff() {
			setTextColor(resources.getColor(R.color.runnerWhite))
			setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
		}
	}

}