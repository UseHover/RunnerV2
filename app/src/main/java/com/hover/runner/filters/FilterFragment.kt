package com.hover.runner.filters

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.hover.runner.R
import com.hover.runner.databinding.FragmentFilterBinding
import com.hover.runner.main.BaseFragment
import com.hover.runner.utils.UIHelper

abstract class FilterFragment : BaseFragment() {
	lateinit var viewModel: FilterViewModel

	private var _binding: FragmentFilterBinding? = null
	val binding get() = _binding!!

	@CallSuper
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentFilterBinding.inflate(inflater, container, false)
		return binding.root
	}

	@CallSuper
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupListeners()
		setupFilterSelections()
		observeFilterData()
	}

	private val searchWatcher: TextWatcher = object : TextWatcher {
		override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
		override fun afterTextChanged(editable: Editable) {}
		override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
			setSearch(charSequence)
		}
	}

	private fun setupListeners() {
		binding.resetBtn.setOnClickListener { reset() }
		binding.filterNow.setOnClickListener { onSave() }
		binding.searchInput.addTextChangedListener(searchWatcher)
		binding.tagInput.setOnClickListener {
			findNavController().navigate(R.id.navigation_filter_selection, bundleOf("type" to "tags"))
		}
		binding.checkboxSucceeded.setOnCheckedChangeListener { _, checked -> viewModel.setStatus("succeeded", checked) }
		binding.checkboxPending.setOnCheckedChangeListener { _, checked -> viewModel.setStatus("pending", checked) }
		binding.checkboxFailed.setOnCheckedChangeListener { _, checked -> viewModel.setStatus("failed", checked) }
		binding.checkboxNone.setOnCheckedChangeListener { _, checked -> viewModel.setStatus(null, checked) }
	}

	private fun setupFilterSelections() {
		binding.searchInput.setText(viewModel.searchString.value)
	}

	@CallSuper
	open fun observeFilterData() {
		viewModel.selectedTags.observe(viewLifecycleOwner) { setTags(it) }
		viewModel.selectedStatuses.observe(viewLifecycleOwner) { setStatuses(it) }
	}

	private fun setSearch(charSequence: CharSequence) {
		if (charSequence.isNotEmpty()) viewModel.setSearch(charSequence.toString())
	}

	private fun setTags(tags: List<String?>) {
		if (!tags.isNullOrEmpty()) binding.tagInput.text = tags.joinToString(", ")
		else binding.tagInput.text = getString(R.string.empty_tags)
	}

	private fun setStatuses(statuses: List<String?>) {
		binding.checkboxSucceeded.isChecked = statuses.contains("succeeded")
		binding.checkboxPending.isChecked = statuses.contains("pending")
		binding.checkboxFailed.isChecked = statuses.contains("failed")
		binding.checkboxNone.isChecked = statuses.contains(null)
	}

	private fun pickDateRange() {
		with(datePicker()) {
			show(this@FilterFragment.parentFragmentManager, toString())
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

	abstract fun onSave()

	@CallSuper
	open fun reset() {
		viewModel.reset()
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

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}