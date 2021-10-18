package com.hover.runner.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {
        fun formatDate(timestamp: Long): String? {
            val pattern = "HH:mm:ss (z) MMM dd, yyyy"
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            return simpleDateFormat.format(timestamp)
        }

        fun formatDateV2(timestamp: Long): String? {
            val pattern = "MMM dd"
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            return simpleDateFormat.format(timestamp)
        }

        fun formatDateV3(timestamp: Long): String? {
            val pattern = "MMM dd, yyyy"
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            return simpleDateFormat.format(timestamp)
        }
    }

}