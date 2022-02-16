package com.hover.runner.actionFilters

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.hover.runner.R
import com.hover.runner.actions.ActionsViewModel
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.FragmentFilterActionsBinding
import com.hover.runner.utils.UIHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ActionsFilterFragment : BaseFragment() {
	private val actionsViewModel: ActionsViewModel by sharedViewModel()

	private var _binding: FragmentFilterActionsBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentFilterActionsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupListeners()
		setupFilterSelections()
		observeFilterData()
	}

	private fun setupListeners() {
		binding.resetBtn.setOnClickListener { reset() }
		binding.filterNow.setOnClickListener { saveFilter() }
		binding.searchInput.addTextChangedListener(searchWatcher)
	}

	private fun saveFilter() {
		findNavController().navigate(R.id.navigation_actions)
	}

	private val searchWatcher: TextWatcher = object : TextWatcher {
		override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
		override fun afterTextChanged(editable: Editable) {}
		override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
			if (charSequence.isNotEmpty()) actionsViewModel.setSearch(charSequence.toString())
		}
	}

	private fun observeFilterData() {
		actionsViewModel.filteredActions.observe(viewLifecycleOwner) { actions ->
			actions?.let { binding.filterNow.text = getString(R.string.cta_filter_actions, actions.size)}
		}
	}

	private fun setupFilterSelections() {
		binding.searchInput.setText(actionsViewModel.searchString.value)
	}

	private fun reset() {
		actionsViewModel.reset()
		binding.searchInput.text = null
		UIHelper.flashMessage(requireContext(), "Filters reset")
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

//	private fun setupCheckboxSelections() {
//		successCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("Succeeded", checked) }
//		pendingCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("Pending", checked) }
//		failureCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("Failed", checked) }
//		noTransactionCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("Empty", checked) }
//		hasParserCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("hasParsers", checked) }
//		onlyWithSimPresentCheckBox.setOnCheckedChangeListener { _, checked -> toggleFilter("simPresent", checked) }
//	}

	private fun toggleFilter(name: String, checked: Boolean) {
//		actionsViewModel.addFilter(name, checked)
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
//		datePickerTextView.setOnClickListener { pickDateRange() }
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

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}