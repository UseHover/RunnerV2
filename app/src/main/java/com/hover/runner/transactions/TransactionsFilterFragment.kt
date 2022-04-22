package com.hover.runner.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.filters.FilterFragment
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class TransactionsFilterFragment : FilterFragment() {
	private lateinit var transactionsViewModel: TransactionsViewModel

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		viewModel = getSharedViewModel<TransactionsViewModel>()
		transactionsViewModel = viewModel as TransactionsViewModel

		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.filterTitle.text = getString(R.string.title_filter_transactions)
		binding.searchInput.hint = getString(R.string.search_hint_transaction)
		binding.statusWrapper.visibility = View.GONE
		binding.dateWrapper.visibility = View.GONE
	}

	override fun onSave() {
		findNavController().navigate(R.id.navigation_transactions)
	}

	override fun observeFilterData() {
		super.observeFilterData()
		transactionsViewModel.filteredTransactions.observe(viewLifecycleOwner) { transactions ->
			transactions?.let { binding.filterNow.text = getString(R.string.cta_filter_transactions, transactions.size)}
		}
	}
}