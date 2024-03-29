package com.hover.runner.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.hover.runner.R
import com.hover.sdk.transactions.Transaction

interface StatusUiTranslator {

	fun getHeaderColor(status: String?, context: Context): Int {
		return when (status) {
			Transaction.PENDING -> ContextCompat.getColor(context, R.color.runnerYellow)
			Transaction.FAILED -> ContextCompat.getColor(context, R.color.runnerRed)
			Transaction.SUCCEEDED -> ContextCompat.getColor(context, R.color.runnerGreen)
			else -> ContextCompat.getColor(context, R.color.secondaryGrey)
		}
	}

	fun getColor(status: String?): Int {
		return when (status) {
			Transaction.PENDING -> R.color.runnerYellow
			Transaction.FAILED -> R.color.runnerRed
			Transaction.SUCCEEDED -> R.color.runnerGreen
			else -> R.color.runnerPrimary
		}
	}

	fun getStatusIcon(status: String?): Int {
		return when (status) {
			Transaction.PENDING -> R.drawable.ic_warning_yellow_24dp
			Transaction.FAILED -> R.drawable.ic_error_red_24dp
			Transaction.SUCCEEDED -> R.drawable.ic_check_circle_green_24dp
			else -> 0
		}
	}

	fun getDarkStatusIcon(status: String?): Int {
		return when (status) {
			Transaction.PENDING -> R.drawable.ic_warning_black_24dp
			Transaction.FAILED -> R.drawable.ic_error_black_24dp
			Transaction.SUCCEEDED -> R.drawable.ic_check_circle_black_24dp
			else -> 0
		}
	}

	fun getActionTitle(status: String?): Int {
		return when (status) {
			Transaction.PENDING -> R.string.pendingStatus_title
			Transaction.FAILED -> R.string.failedStatus_title
			Transaction.SUCCEEDED -> R.string.successStatus_title
			else -> R.string.unrunStatus_title
		}
	}

	fun getTransactionTitle(status: String?): Int {
		return when (status) {
			Transaction.PENDING -> R.string.transaction_det_pending
			Transaction.FAILED -> R.string.transaction_det_failed
			else -> R.string.transaction_det_success
		}
	}

	fun getStatusDescription(status: String?): Int {
			return when (status) {
			Transaction.PENDING -> R.string.pendingStatus_desc
			Transaction.FAILED -> R.string.failedStatus_desc
			else -> R.string.successStatus_title
		}
	}

	fun getLearnText(status: String?): Int {
		return when (status) {
			Transaction.PENDING -> R.string.pendingStatus_linkText
			Transaction.FAILED -> R.string.failedStatus_linkText
			else -> R.string.success_label
		}
	}

	fun getWebTitle(status: String): Int {
		return when (status) {
			Transaction.PENDING -> R.string.pending_transaction
			else -> R.string.failed_transaction
		}
	}
}