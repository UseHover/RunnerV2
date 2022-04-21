package com.hover.runner.testRuns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.actions.ActionRecyclerAdapter
import com.hover.runner.databinding.FragmentTestRunDetailsBinding
import com.hover.runner.transactions.TransactionsRecyclerAdapter
import com.hover.runner.utils.DateUtils
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.transactions.Transaction
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RunDetailsFragment : Fragment(), ActionRecyclerAdapter.ActionClickListener, TransactionsRecyclerAdapter.TransactionClickListener {

	private val viewModel: RunsViewModel by sharedViewModel()
	private var _binding: FragmentTestRunDetailsBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTestRunDetailsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel.run.observe(viewLifecycleOwner) { it?.let { fillDetails(it) } }
		viewModel.actions.observe(viewLifecycleOwner) { it?.let { addActions(it) } }
		viewModel.transactions.observe(viewLifecycleOwner) { it?.let { addTransactions(it) } }
		viewModel.load(requireArguments().getLong(RUN_ID))
		binding.delete.setOnClickListener { showDeleteDialog() }
	}

	private fun fillDetails(run: TestRun?) {
		run?.let {
			binding.toolbar.title = run.name
			binding.frequencyValue.text = resources.getStringArray(R.array.frequency_array)[run.frequency]
			binding.startValue.text = DateUtils.humanFriendlyDateTime(run.start_at)
			if (run.start_at > System.currentTimeMillis())
				binding.startLabel.text = getString(R.string.label_next_start)
			else
				binding.startLabel.text = getString(R.string.label_last_start)
			binding.delete.visibility = VISIBLE
		}
	}

	private fun addActions(actions: List<HoverAction>) {
		binding.actonsRecycler.setLayoutManagerToLinear()
		binding.actonsRecycler.adapter = ActionRecyclerAdapter(actions, hashMapOf(), this)
	}

	private fun addTransactions(transactions: List<Transaction>) {
		binding.transactionsRecycler.setLayoutManagerToLinear()
		binding.transactionsRecycler.adapter = TransactionsRecyclerAdapter(transactions, this)
		setStatusCounts(transactions)
		binding.transactionsTitle.visibility = if (transactions.isNullOrEmpty()) View.GONE else View.VISIBLE
		binding.transactionsDivider.visibility = if (transactions.isNullOrEmpty()) View.GONE else View.VISIBLE
		binding.transactionsRecycler.visibility = if (transactions.isNullOrEmpty()) View.GONE else View.VISIBLE

		binding.delete.visibility = if (transactions.isNullOrEmpty()) View.VISIBLE else View.GONE
		binding.error.visibility = if (transactions.isNotEmpty() && !viewModel.actions.value.isNullOrEmpty() && transactions.size < viewModel.actions.value!!.size) View.VISIBLE else View.GONE
	}

	private fun setStatusCounts(transactions: List<Transaction>) {
		binding.transactionSummary.transactionCount.text = transactions.size.toString()
		binding.transactionSummary.successCount.text = transactions.count { it.status == Transaction.SUCCEEDED}.toString()
		binding.transactionSummary.pendingCount.text = transactions.count { it.status == Transaction.PENDING}.toString()
		binding.transactionSummary.failedCount.text = transactions.count { it.status == Transaction.FAILED}.toString()
	}

	private fun showDeleteDialog() {
		val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity()).apply {
			setTitle(R.string.delete_title)
			setMessage(R.string.delete_message)
			setPositiveButton(R.string.delete) { dialog, id -> delete() }
			setNegativeButton(R.string.cancel, null)
		}
		val alert = builder.create()
		alert.show()
	}

	private fun delete() {
		viewModel.deleteRun()
		UIHelper.flashMessage(requireContext(), view, getString(R.string.run_deleted))
		findNavController().popBackStack()
	}

	override fun onActionItemClick(actionId: String, titleTextView: View) {
		val bundle = bundleOf("action_id" to actionId)
		findNavController().navigate(R.id.navigation_actionDetails, bundle)
	}

	override fun onItemClick(uuid: String) {
		findNavController().navigate(R.id.navigation_transactionDetails, bundleOf("uuid" to uuid))
	}
}