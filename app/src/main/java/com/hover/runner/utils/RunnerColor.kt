package com.hover.runner.utils

import android.content.Context
import com.hover.runner.R

open class RunnerColor(val context: Context) {
    val RED = context.resources.getColor(R.color.colorRed)
    val YELLOW = context.resources.getColor(R.color.colorYellow)
    val GREEN: Int = context.resources.getColor(R.color.colorGreen)
    val DARK = context.resources.getColor(R.color.colorPrimaryDark)
    val WHITE = context.resources.getColor(R.color.colorHoverWhite)
    val SILVER = context.resources.getColor(R.color.colorSecondaryGrey)
    val GRAY = context.resources.getColor(R.color.colorMainGrey)

    fun get(intRes: Int): Int = context.resources.getColor(intRes)
}