package com.hover.runner.utils

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView
import com.hover.runner.R
import timber.log.Timber

class TextViewUtils {
	companion object {

		fun TextView.underline() {
			underlineText(this, this.text.toString())
		}

		fun TextView.underline(newValue: String?) {
			newValue?.let { underlineText(this, it) }
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
	}
}