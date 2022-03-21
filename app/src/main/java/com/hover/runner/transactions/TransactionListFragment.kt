package com.hover.runner.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.databinding.FragmentTransactionsBinding
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TransactionListFragment : Fragment(), TransactionsRecyclerAdapter.TransactionClickListener {
	private var _binding: FragmentTransactionsBinding? = null
	private val binding get() = _binding!!

	private val transactionsViewModel: TransactionsViewModel by sharedViewModel()
	private lateinit var filterTextView: TextView
	private lateinit var emptyInfoLayout: LinearLayout
	private lateinit var progressBar: ProgressBar
	private lateinit var homeTransactionsRecyclerView: RecyclerView
	private lateinit var emptyStateView: RelativeLayout

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTransactionsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		setupRecyclerView()
		observeTransactionsList()
//		filterTextView.setOnClickListener { transactionNavigationInterface.navigateTransactionFilterFragment() }
	}

	private fun initViews() {
		filterTextView = binding.transactionFilterId
		progressBar = binding.recyclerViewState.progressState1
		emptyInfoLayout = binding.recyclerViewState.emptyInfoLayout
		emptyStateView = binding.recyclerViewState.layoutForEmptyStateId
		homeTransactionsRecyclerView = binding.recyclerView
	}

	private fun setupRecyclerView() {
		homeTransactionsRecyclerView.setLayoutManagerToLinear()
		homeTransactionsRecyclerView.setHasFixedSize(false)
	}

	private fun observeTransactionsList() {
		transactionsViewModel.transactions.observe(viewLifecycleOwner) { it ->
			if (it.isNullOrEmpty()) showLoadingView()
			else {
				showRecyclerView()
				updateFilterTextStyle(it.size)
				homeTransactionsRecyclerView.adapter = TransactionsRecyclerAdapter(it, this)
			}
		}
	}

	private fun updateFilterTextStyle(currentTransactionListSize: Int) {
		filterTextView.visibility = GONE
//		val initialTransactionListSize: Int = transactionsViewModel.filter_transactionsTotal()
//		val isFilterOn: Boolean = currentTransactionListSize < initialTransactionListSize
//
//		if (isFilterOn) filterTextView.styleAsFilterOn()
//		else filterTextView.styleAsFilterOff()
	}

	private fun showLoadingView() {
		homeTransactionsRecyclerView.visibility = View.GONE
		progressBar.visibility = View.VISIBLE
		emptyInfoLayout.visibility = View.GONE
		emptyStateView.visibility = View.GONE

	}

	private fun showEmptyView() {
		homeTransactionsRecyclerView.visibility = View.GONE
		progressBar.visibility = View.GONE
		emptyInfoLayout.visibility = View.VISIBLE
		emptyStateView.visibility = View.VISIBLE
	}

	private fun showRecyclerView() {
		homeTransactionsRecyclerView.visibility = View.VISIBLE
		progressBar.visibility = View.GONE
		emptyInfoLayout.visibility = View.GONE
		emptyStateView.visibility = View.GONE
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onItemClick(uuid: String) {
		findNavController().navigate(R.id.navigation_transactionDetails, bundleOf("uuid" to uuid))
	}
}