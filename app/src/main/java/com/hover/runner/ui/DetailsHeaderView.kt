package com.hover.runner.ui

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.hover.runner.utils.StatusUiTranslator
import com.hover.runner.databinding.DetailsHeaderBinding
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.utils.UIHelper
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.transactions.Transaction

class DetailsHeaderView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet), StatusUiTranslator {

	private var binding: DetailsHeaderBinding = DetailsHeaderBinding.inflate(LayoutInflater.from(context), this, true)

	fun setAction(action: HoverAction) {
		binding.headerTitle.text = action.name
		binding.headerSubtitle.text = action.public_id
	}

	fun setTransaction(transaction: RunnerTransaction, activity: Activity) {
		binding.headerTitle.text = transaction.getDate()!!
		binding.headerSubtitle.text = transaction.uuid
		setStatus(transaction.status, activity)
	}

	fun setStatus(status: String?, activity: Activity) {
		binding.headerStatus.setText(getActionTitle(status))
		binding.headerDescription.setText(getStatusDescription(status))
		binding.headerLearn.setText(resources.getString(getLearnText(status)))

		binding.headerDescription.visibility = if (status != Transaction.PENDING && status != Transaction.FAILED) View.GONE else View.VISIBLE
		binding.headerLearn.visibility = if (status != Transaction.PENDING && status != Transaction.FAILED) View.GONE else View.VISIBLE
		updateColorsAndIcons(status, activity)
		addListeners(status, activity)
	}

	fun updateColorsAndIcons(status: String?, activity: Activity) {
		binding.detailHeaderRoot.setBackgroundColor(getHeaderColor(status, context))
		binding.headerDescription.setCompoundDrawablesWithIntrinsicBounds(getDarkStatusIcon(status), 0, 0, 0)
		UIHelper.changeStatusBarColor(activity, resources.getColor(getColor(status)))
	}

	private fun addListeners(status: String?, activity: Activity) {
		binding.headerLearn.setOnClickListener {
//			navInterface.navWebView(resources.getString(getWebTitle(status)), resources.getString(R.string.pendingStatus_url))
		}
		binding.headerTitle.setOnClickListener { activity.onBackPressed() }
	}

}