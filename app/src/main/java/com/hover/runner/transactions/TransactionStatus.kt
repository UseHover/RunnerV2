package com.hover.runner.transactions

import com.hover.runner.R
import com.hover.sdk.transactions.Transaction

abstract class TransactionStatus {
    companion object {

        fun hasTransaction(status: String) : Boolean {
            return when(status)  {
                Transaction.PENDING -> true
                Transaction.FAILED -> true
                Transaction.SUCCEEDED -> true
                else -> false
            }
        }

        fun getColor(status: String?) : Int{
            return when(status) {
                Transaction.PENDING -> R.color.colorYellow
                Transaction.FAILED -> R.color.colorRed
                Transaction.SUCCEEDED -> R.color.colorGreen
                else -> R.color.colorPrimaryDark
            }
        }

        fun getDrawable(status: String?) : Int {
            return when (status) {
                Transaction.PENDING -> R.drawable.ic_warning_yellow_24dp
                Transaction.FAILED -> R.drawable.ic_error_red_24dp
                Transaction.SUCCEEDED -> R.drawable.ic_check_circle_green_24dp
                else -> 0
            }
        }

    }
}