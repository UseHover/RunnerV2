package com.hover.runner.filter.filter_transactions.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.util.Pair
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.hover.runner.R
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.TransactionFilterFragmentBinding
import com.hover.runner.filter.enumValue.FilterForEnum
import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.filter.filter_transactions.navigation.FilterTransactionNavigationInterface
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.TextViewUtils.Companion.activateView
import com.hover.runner.utils.TextViewUtils.Companion.deactivateView
import com.hover.runner.utils.TextViewUtils.Companion.underline
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class TransactionsFilterFragment : BaseFragment() {

	private var _binding: TransactionFilterFragmentBinding? = null
	private val binding get() = _binding!!

	private val transactionViewModel: TransactionViewModel by sharedViewModel()
	private lateinit var filterTransactionNavigationInterface: FilterTransactionNavigationInterface

	private lateinit var loadingProgressBar: ProgressBar
	private lateinit var resetTextView: TextView
	private lateinit var countryEntryTextView: TextView
	private lateinit var networkEntryTextView: TextView
	private lateinit var datePickerTextView: TextView
	private lateinit var actionEntryTextView: TextView
	private lateinit var successCheckBox: AppCompatCheckBox
	private lateinit var pendingCheckBox: AppCompatCheckBox
	private lateinit var failureCheckBox: AppCompatCheckBox
	private lateinit var toolBarTextView: TextView
	private lateinit var filterLayout: LinearLayout
	private lateinit var showTransactionsTextView: TextView

	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View {
		_binding = TransactionFilterFragmentBinding.inflate(inflater, container, false)
		initNavigationInterface()
		return binding.root
	}

	private fun initNavigationInterface() {
		filterTransactionNavigationInterface = activity as FilterTransactionNavigationInterface
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		setupToolBarViews()
		setupFilterSelections()
		setupResetFilter()
		observeFilterParameters()
		setupShowFilteredTransactions()
		observeFilterTransactions()
	}

	private fun initViews() {
		toolBarTextView = binding.transactionFilterBackId
		filterLayout = binding.entryFilterView
		showTransactionsTextView = binding.showActionsId
		loadingProgressBar = binding.filterProgressBar
		resetTextView = binding.resetId
		countryEntryTextView = binding.countryEntryId
		networkEntryTextView = binding.networkEntryId
		actionEntryTextView = binding.actionsSelectEntryId
		successCheckBox = binding.checkboxSuccess
		pendingCheckBox = binding.checkboxPending
		failureCheckBox = binding.checkboxFail
		datePickerTextView = binding.dateRangeEntryId
	}

	private fun setupToolBarViews() {
		toolBarTextView.setOnClickListener { navigateBack() }
		resetTextView.underline()
	}

	private fun setupShowFilteredTransactions() {
		showTransactionsTextView.setOnClickListener {
			val filteredTransactions = transactionViewModel.filter_getTransactions()
			transactionViewModel.updateTransactionsLiveData(filteredTransactions)
			navigateBack()
		}
	}

	private fun setupFilterSelections() {
		setupFilterEntryBoxSelections()
		setupCheckboxSelections()
	}

	private fun observeFilterParameters() {
		transactionViewModel.transactionFilterParametersMutableLiveData.observe(viewLifecycleOwner) {
			updateFilterEntryData(it)
			updateFilterCheckboxes(it)
		}
	}

	private fun observeFilterTransactions() {
		observeFilterData()
		observeFilterLoadingStatus()
	}

	private fun observeFilterData() {
		transactionViewModel.filteredTransactionsMutableLiveData.observe(viewLifecycleOwner) { transactions ->
			if (transactions != null) {
				with(showTransactionsTextView) {
					if (transactions.isNotEmpty()) {
						isClickable = true
						setBackgroundColor(resources.getColor(R.color.colorPrimary))
						val suffixAction = if (transactions.size == 1) "transactions " else "transaction"
						text = String.format(Locale.getDefault(), "Show %d %s", transactions.size, suffixAction)
					}
					else {
						isClickable = false
						setBackgroundColor(resources.getColor(R.color.colorMainGrey))
						text = resources.getString(R.string.no_transactions_filter_result)
					}
				}
			}
		}
	}

	private fun observeFilterLoadingStatus() {
		transactionViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { hasLoaded ->
			if (!hasLoaded) {
				with(showTransactionsTextView) {
					isClickable = false
					setBackgroundColor(resources.getColor(R.color.colorPrimary))
					text = resources.getString(R.string.loadingText)
				}
			}
		}
	}


	private fun setupResetFilter() {
		resetTextView.setOnClickListener { handleResetTextClick() }
		observeForResetTextView()
	}

	private fun handleResetTextClick() {
		transactionViewModel.getAllTransactions()
		transactionViewModel.filter_reset()
	}

	private fun observeForResetTextView() {
		with(transactionViewModel) {
			transactionsParentTotalLiveData.observe(viewLifecycleOwner) {
				if (filter_transactionsTotal() != 0 && filter_transactionsTotal() < it) activateReset()
				else deactivateReset()
			}
		}
	}


	private fun updateFilterEntryData(parameters: TransactionFilterParameters) {
		with(parameters) {
			actionEntryTextView.text = getActionIdsAsString()
			countryEntryTextView.text = getCountryListAsString()
			networkEntryTextView.text = getNetworkNamesAsString()
			datePickerTextView.text = getDateRangeValue(requireContext())
		}
	}

	private fun updateFilterCheckboxes(parameters: TransactionFilterParameters) {
		with(parameters) {
			successCheckBox.isChecked = isTransactionSuccessfulIncluded()
			pendingCheckBox.isChecked = isTransactionPendingIncluded()
			failureCheckBox.isChecked = isTransactionFailedIncluded()
		}
	}

	private fun setupCheckboxSelections() {
		successCheckBox.setOnCheckedChangeListener { _, checked: Boolean ->
			transactionViewModel.filter_IncludeSucceededTransactions(checked)
		}
		pendingCheckBox.setOnCheckedChangeListener { _, checked ->
			transactionViewModel.filter_IncludePendingTransactions(checked)
		}
		failureCheckBox.setOnCheckedChangeListener { _, checked ->
			transactionViewModel.filter_IncludeFailedTransactions(checked)
		}
	}

	private fun setupFilterEntryBoxSelections() {
		actionEntryTextView.setOnClickListener { filterTransactionNavigationInterface.navigateToSelectActionsFragment() }
		countryEntryTextView.setOnClickListener { filterTransactionNavigationInterface.navigateToSelectCountriesFragment(FilterForEnum.TRANSACTIONS) }
		networkEntryTextView.setOnClickListener { filterTransactionNavigationInterface.navigateToSelectNetworksFragment(FilterForEnum.TRANSACTIONS) }
		datePickerTextView.setOnClickListener { pickDateRange() }
	}

	private fun pickDateRange() {
		with(datePicker()) {
			show(this@TransactionsFilterFragment.parentFragmentManager, toString())
		}
	}

	private fun datePicker(): MaterialDatePicker<Pair<Long, Long>> {
		val builder = MaterialDatePicker.Builder.dateRangePicker()
		val constraintsBuilder = CalendarConstraints.Builder()
		builder.setCalendarConstraints(constraintsBuilder.build())

		val picker = builder.setTitleText(resources.getString(R.string.selected_range)).build()
		picker.addOnPositiveButtonClickListener { selection: Pair<Long, Long>? ->
			selection?.let { transactionViewModel.filter_UpdateDateRange(it.first, it.second) }
		}
		return picker
	}

	private fun deactivateReset() {
		resetTextView.deactivateView()
	}

	private fun activateReset() {
		resetTextView.activateView()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}