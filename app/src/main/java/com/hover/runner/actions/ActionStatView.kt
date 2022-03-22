package com.hover.runner.actions

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.hover.runner.databinding.ActionStatLayoutBinding
import com.hover.sdk.transactions.Transaction


class ActionStatView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
	private val binding: ActionStatLayoutBinding = ActionStatLayoutBinding .inflate(LayoutInflater.from(context), this, true)

	fun setup(actionsViewModel: ActionsViewModel, owner: LifecycleOwner) {
		setClicks(actionsViewModel)
		observeValues(actionsViewModel, owner)
	}

	private fun observeValues(actionsViewModel: ActionsViewModel, owner: LifecycleOwner) {
		actionsViewModel.statuses.observe(owner) {
			binding.successCount.text = getString(it, Transaction.SUCCEEDED)
			binding.pendingCount.text = getString(it, Transaction.PENDING)
			binding.failedCount.text = getString(it, Transaction.FAILED)
			binding.notYetRunCount.text = (it.values.count { it == null }).toString() + " Not yet tested";
		}
	}

	private fun setClicks(actionsViewModel: ActionsViewModel) {
		binding.successCount.setOnClickListener { if(notZero(binding.successCount)) actionsViewModel.filterOutActions(Transaction.SUCCEEDED) }
		binding.pendingCount.setOnClickListener { if(notZero(binding.pendingCount)) actionsViewModel.filterOutActions(Transaction.PENDING) }
		binding.failedCount.setOnClickListener { if(notZero(binding.failedCount)) actionsViewModel.filterOutActions(Transaction.FAILED) }
		binding.notYetRunCount.setOnClickListener { if(notZero(binding.notYetRunCount)) actionsViewModel.filterOutActions(null) }
	}
	private fun notZero(text: TextView) :Boolean {
		return text.text.first() != '0';
	}

	private fun getString(map: HashMap<String, String?>, status: String) : String {
		return map.count { entry -> entry.value == status  }.toString() +" "+ status
	}

}