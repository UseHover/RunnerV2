package com.hover.runner.filter.filter_actions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.util.Pair
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.hover.runner.R
import com.hover.runner.actions.ActionsViewModel
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.ActionFilterFragmentBinding
import com.hover.runner.utils.TextViewUtils.Companion.activateView
import com.hover.runner.utils.TextViewUtils.Companion.deactivateView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class ActionsFilterFragment : BaseFragment() {

	private lateinit var loadingProgressBar: ProgressBar
	private lateinit var resetTextView: TextView
	private lateinit var countryEntryTextView: TextView
	private lateinit var networkEntryTextView: TextView
	private lateinit var categoryEntryTextView: TextView
	private lateinit var datePickerTextView: TextView
	private lateinit var searchActionEditText: EditText
	private lateinit var successCheckBox: AppCompatCheckBox
	private lateinit var pendingCheckBox: AppCompatCheckBox
	private lateinit var failureCheckBox: AppCompatCheckBox
	private lateinit var noTransactionCheckBox: AppCompatCheckBox
	private lateinit var hasParserCheckBox: AppCompatCheckBox
	private lateinit var onlyWithSimPresentCheckBox: AppCompatCheckBox
	private lateinit var filterLayout: LinearLayout
	private lateinit var showActionsTextView: TextView

	private val actionsViewModel: ActionsViewModel by sharedViewModel()
	private var _binding: ActionFilterFragmentBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = ActionFilterFragmentBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		setupListeners()
		setupFilterSelections()
		observeFilterParameters()
		observeFilterData()
	}

	private fun initViews() {
		filterLayout = binding.entryFilterView
		showActionsTextView = binding.showActionsId
		loadingProgressBar = binding.filterProgressBar
		resetTextView = binding.resetId
		countryEntryTextView = binding.countryEntryId
		networkEntryTextView = binding.networkEntryId
		categoryEntryTextView = binding.categoryEntryId
		searchActionEditText = binding.searchEditId
		successCheckBox = binding.checkboxSuccess
		pendingCheckBox = binding.checkboxPending
		failureCheckBox = binding.checkboxFail
		noTransactionCheckBox = binding.checkboxNoTransaction
		hasParserCheckBox = binding.checkboxParsers
		onlyWithSimPresentCheckBox = binding.checkboxSim
		datePickerTextView = binding.dateRangeEditId
	}

	private fun setupListeners() {
		resetTextView.setOnClickListener { actionsViewModel.setFilter(null) }
		binding.actionFilterBackId.setOnClickListener { navigateBack() }
	}

	private fun setupFilterSelections() {
		setupFilterEntryBoxSelections()
		setupCheckboxSelections()
	}

	private fun observeFilterParameters() {
		actionsViewModel.filterString.observe(viewLifecycleOwner) {
			updateFilterEntryData(actionsViewModel.generateFilterMap())
			updateFilterCheckboxes(actionsViewModel.generateFilterMap())
		}
	}

	private fun observeFilterData() {
		actionsViewModel.filteredActions.observe(viewLifecycleOwner) { actions ->
			with(showActionsTextView) {
				isClickable = !actions.isNullOrEmpty()
				setBackgroundColor(resources.getColor(if (actions.isNullOrEmpty()) R.color.mainGrey else R.color.runnerPrimary))

				if (actions == null) {
					text = resources.getString(R.string.loadingText)
					showReset(false)
				} else if (actions.isNotEmpty()) {
					val suffixAction = if (actions.size == 1) "action " else "action"
					text = String.format(Locale.getDefault(), "Show %d %s", actions.size, suffixAction)
					showReset(actions.size != actionsViewModel.allActions.value?.size)
				} else {
					text = resources.getString(R.string.no_actions_filter_result)
					showReset(!actionsViewModel.allActions.value.isNullOrEmpty())
				}
			}
		}
	}

	private fun updateFilterEntryData(filterMap: Map<String, String>) {
		with(filterMap) {
//			searchActionEditText.setText(getActionIdOrRootCode())
//			countryEntryTextView.text = getCountryListAsString()
//			categoryEntryTextView.text = getCategoryListAsString()
//			networkEntryTextView.text = getNetworkNamesAsString()
//			datePickerTextView.text = getDateRangeValue(requireContext())
		}
	}

	private fun updateFilterCheckboxes(filterMap: Map<String, String>) {
		with(filterMap) {
//			successCheckBox.isChecked = isTransactionSuccessfulIncluded()
//			pendingCheckBox.isChecked = isTransactionPendingIncluded()
//			failureCheckBox.isChecked = isTransactionFailedIncluded()
//			noTransactionCheckBox.isChecked = hasNoTransaction
//			hasParserCheckBox.isChecked = hasParser
//			onlyWithSimPresentCheckBox.isChecked = onlyWithSimPresent
		}
	}

	private fun setupCheckboxSelections() {
		successCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("Succeeded", checked) }
		pendingCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("Pending", checked) }
		failureCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("Failed", checked) }
		noTransactionCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("Empty", checked) }
		hasParserCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("hasParsers", checked) }
		onlyWithSimPresentCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("simPresent", checked) }
	}

	private fun toggleFilter(name: String, checked: Boolean) {
		actionsViewModel.addFilter(name, checked)
	}

	private fun setupFilterEntryBoxSelections() {
//		searchActionEditText.addTextChangedListener(searchActionEditTextWatcher)
//		countryEntryTextView.setOnClickListener {
//			filterActionNavigationInterface.navigateToSelectCountriesFragment(FilterForEnum.ACTIONS)
//		}
//		categoryEntryTextView.setOnClickListener { filterActionNavigationInterface.navigateToSelectCategoriesFragment() }
//		networkEntryTextView.setOnClickListener {
//			filterActionNavigationInterface.navigateToSelectNetworksFragment(FilterForEnum.ACTIONS)
//		}
		datePickerTextView.setOnClickListener { pickDateRange() }
	}

	private fun pickDateRange() {
		with(datePicker()) {
			show(this@ActionsFilterFragment.parentFragmentManager, toString())
		}
	}

	private fun datePicker(): MaterialDatePicker<Pair<Long, Long>> {
		val builder = MaterialDatePicker.Builder.dateRangePicker()
		val constraintsBuilder = CalendarConstraints.Builder()
		builder.setCalendarConstraints(constraintsBuilder.build())

		val picker = builder.setTitleText(resources.getString(R.string.selected_range)).build()
		picker.addOnPositiveButtonClickListener { selection: Pair<Long, Long>? ->
//			selection?.let { actionsViewModel.setDate(it.first, it.second) }
		}
		return picker
	}

	private fun showReset(show: Boolean) {
		if (show) resetTextView.activateView() else resetTextView.deactivateView()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}