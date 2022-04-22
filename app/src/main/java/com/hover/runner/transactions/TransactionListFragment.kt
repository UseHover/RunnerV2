package com.hover.runner.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.actions.ActionRecyclerAdapter
import com.hover.runner.databinding.FragmentTransactionsBinding
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.sdk.transactions.Transaction
import org.eazegraph.lib.models.PieModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class TransactionListFragment : Fragment(), TransactionsRecyclerAdapter.TransactionClickListener {
	private var _binding: FragmentTransactionsBinding? = null
	private val binding get() = _binding!!

	private val transactionsViewModel: TransactionsViewModel by sharedViewModel()
	private var transactionRecycler: TransactionsRecyclerAdapter? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTransactionsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.recyclerView.setLayoutManagerToLinear()
		observeTransactionsList()
		binding.filterBtn.setOnClickListener { view.findNavController().navigate(R.id.navigation_transactionFilter) }
	}

	private fun observeTransactionsList() {
		transactionsViewModel.filteredTransactions.observe(viewLifecycleOwner) { transactions ->
			if (transactions.isNullOrEmpty()) {
				binding.emptyState.root.visibility = View.VISIBLE
				binding.graph.root.visibility = View.GONE
			} else {
				showTransactions(transactions)
			}
		}
	}

	private fun showTransactions(ts: List<Transaction>) {
		setFilterState(ts.size)
		binding.emptyState.root.visibility = View.GONE
		transactionRecycler = TransactionsRecyclerAdapter(ts, this)
		binding.recyclerView.adapter = transactionRecycler
		updatePie(ts)
	}

	private fun updatePie(ts: List<Transaction>) {
		binding.graph.root.visibility = View.VISIBLE
		binding.graph.pie.clearChart()
		binding.graph.pie.innerValueString = getString(R.string.count_label, ts.count())
		binding.graph.pie.addPieSlice(PieModel(ts.count { it.status == "pending" }.toFloat(), resources.getColor(R.color.runnerYellow)))
		binding.graph.pie.addPieSlice(PieModel(ts.count { it.status == "succeeded" }.toFloat(), resources.getColor(R.color.runnerGreen)))
		binding.graph.pie.addPieSlice(PieModel(ts.count { it.status == "failed" }.toFloat(), resources.getColor(R.color.runnerRed)))
		binding.graph.pendingTotal.text = getString(R.string.pending_count_label, ts.count{ it.status == "pending" })
		binding.graph.succeededTotal.text = getString(R.string.succeeded_count_label, ts.count{ it.status == "succeeded" })
		binding.graph.failedTotal.text = getString(R.string.failed_count_label, ts.count{ it.status == "failed" })
	}

	private fun setFilterState(listSize: Int) {
		binding.filterBtn.setTextColor(ContextCompat.getColor(requireContext(), if (showingAll(listSize)) R.color.runnerWhite else R.color.runnerPrimary))
		binding.filterBtn.setCompoundDrawablesWithIntrinsicBounds(if (showingAll(listSize)) 0 else R.drawable.ic_dot_purple_24dp, 0, 0, 0)
	}

	private fun showingAll(transactionsSize: Int): Boolean {
		return transactionsViewModel.filteredTransactions.value == null || transactionsSize == transactionsViewModel.allTransactions.value!!.size
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onItemClick(uuid: String) {
		findNavController().navigate(R.id.navigation_transactionDetails, bundleOf("uuid" to uuid))
	}
}