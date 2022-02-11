package com.hover.runner.utils

import android.content.Context
import com.hover.runner.R

open class RunnerColor(val context: Context) {
	val RED = context.resources.getColor(R.color.runnerRed)
	val YELLOW = context.resources.getColor(R.color.runnerYellow)
	val GREEN: Int = context.resources.getColor(R.color.runnerGreen)
	val DARK = context.resources.getColor(R.color.runnerDark)
	val WHITE = context.resources.getColor(R.color.runnerWhite)
	val SILVER = context.resources.getColor(R.color.secondaryGrey)
	val GRAY = context.resources.getColor(R.color.mainGrey)

	fun get(intRes: Int): Int = context.resources.getColor(intRes)
}