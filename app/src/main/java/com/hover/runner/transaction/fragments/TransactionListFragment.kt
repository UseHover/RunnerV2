package com.hover.runner.transaction.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.hover.runner.transaction.adapters.TransactionRecyclerAdapter
import com.hover.runner.transaction.listeners.TransactionClickListener
import com.hover.runner.transaction.navigation.TransactionNavigationInterface
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.TextViewUtils.Companion.styleAsFilterOff
import com.hover.runner.utils.TextViewUtils.Companion.styleAsFilterOn
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TransactionListFragment : Fragment(), TransactionClickListener {
	private var _binding: FragmentTransactionsBinding? = null
	private val binding get() = _binding!!

	private val transactionViewModel: TransactionViewModel by sharedViewModel()
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
		initTransactions()
		observeLoadingStatus()
		observeTransactionsList()
//		filterTextView.setOnClickListener { transactionNavigationInterface.navigateTransactionFilterFragment() }
	}

	override fun onResume() {
		super.onResume()
		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).DARK)
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

	private fun initTransactions() {
		transactionViewModel.getAllTransactions()
	}

	private fun observeLoadingStatus() {
		transactionViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { hasLoaded ->
			if (hasLoaded) showRecyclerView() else showLoadingView()
		}
	}

	private fun observeTransactionsList() {
		transactionViewModel.transactionsLiveData.observe(viewLifecycleOwner) { transactions ->
			if (transactions != null) {
				updateFilterTextStyle(transactions.size)

				if (transactions.isEmpty()) {
					showEmptyView()
				}
				else {
					homeTransactionsRecyclerView.adapter =
						TransactionRecyclerAdapter(transactions, this)
				}
			}
		}
	}

	private fun updateFilterTextStyle(currentTransactionListSize: Int) {
		val initialTransactionListSize: Int = transactionViewModel.filter_transactionsTotal()
		val isFilterOn: Boolean = currentTransactionListSize < initialTransactionListSize

		if (isFilterOn) filterTextView.styleAsFilterOn()
		else filterTextView.styleAsFilterOff()
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

	override fun onTransactionItemClicked(uuid: String) {
		findNavController().navigate(R.id.navigation_transactionDetails, bundleOf("uuid" to uuid))
	}
}