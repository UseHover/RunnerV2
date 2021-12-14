package com.hover.runner.customViews.detailsTopLayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.hover.runner.R
import com.hover.runner.transaction.StatusUIHelper
import com.hover.runner.utils.RunnerColor
import com.hover.sdk.transactions.Transaction

abstract class TopDetailsContentChooser(context: Context, attributeSet: AttributeSet) :
	LinearLayout(context, attributeSet) {
	fun getLayoutBackground(status: String): Int {
		return when (status) {
			Transaction.PENDING -> RunnerColor(context).YELLOW
			Transaction.FAILED -> RunnerColor(context).RED
			Transaction.SUCCEEDED -> RunnerColor(context).GREEN
			else -> RunnerColor(context).SILVER
		}
	}

	fun getTitleTextColor(status: String): Int {
//		return if (StatusUIHelper.hasTransaction(status)) RunnerColor(context).WHITE
//		else
		return RunnerColor(context).DARK
	}

	fun getTitleTextCompoundDrawable(status: String): Int {
//		return if (StatusUIHelper.hasTransaction(status)) R.drawable.ic_arrow_back_white_24dp
//		else
		return R.drawable.ic_arrow_back_black_24dp
	}

	fun getSubTitleTextColor(status: String): Int {
		return getTitleTextColor(status)
	}

	fun getDescTitle(status: String, detailScreenType: DetailScreenType): Int {
		return if (detailScreenType == DetailScreenType.ACTION) {
			when (status) {
				Transaction.PENDING -> R.string.pendingStatus_title
				Transaction.FAILED -> R.string.failedStatus_title
				Transaction.SUCCEEDED -> R.string.successStatus_title
				else -> R.string.unrunStatus_title
			}
		}
		else { // TRANSACTION DETAILS
			when (status) {
				Transaction.PENDING -> R.string.transaction_det_pending
				Transaction.FAILED -> R.string.transaction_det_failed
				else -> R.string.transaction_det_success
			}
		}
	}

	fun getDescContent(status: String, detailScreenType: DetailScreenType): Int {
		return if (detailScreenType == DetailScreenType.ACTION) {
			when (status) {
				Transaction.PENDING -> R.string.pendingStatus_desc
				Transaction.FAILED -> R.string.failedStatus_desc
				Transaction.SUCCEEDED -> R.string.successStatus_title
				else -> R.string.emptyString
			}
		}
		else when (status) { // TRANSACTION DETAILS
			Transaction.PENDING -> R.string.pendingStatus_desc
			Transaction.FAILED -> R.string.failedStatus_desc
			else -> R.string.successStatus_title
		}
	}

	fun getDescCompoundDrawable(status: String): Int {
		return when (status) {
			Transaction.PENDING -> R.drawable.ic_warning_black_24dp
			Transaction.FAILED -> R.drawable.ic_error_black_24dp
			else -> R.drawable.ic_check_circle_black_24dp
		}
	}

	fun getDescVisibility(status: String): Int {
		return if (status == Transaction.SUCCEEDED) View.GONE else View.VISIBLE
	}

	fun getDescLinkLabel(status: String): Int {
		return when (status) {
			Transaction.PENDING -> R.string.pendingStatus_linkText
			Transaction.FAILED -> R.string.failedStatus_linkText
			else -> R.string.success_label
		}
	}

	fun getLink(): Int {
		return R.string.pendingStatus_url
	}

	fun getWebTitle(status: String): Int {
		return when (status) {
			Transaction.PENDING -> R.string.pending_transaction
			else -> R.string.failed_transaction
		}
	}
}