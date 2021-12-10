package com.hover.runner.filter.selectionFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.FilterByCountryBinding
import com.hover.runner.filter.checkbox.CheckBoxItem
import com.hover.runner.filter.checkbox.CheckboxItemAdapter
import com.hover.runner.filter.enumValue.FilterForEnum
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.TextViewUtils.Companion.activateView
import com.hover.runner.utils.TextViewUtils.Companion.deactivateView
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SelectCountryFragment : BaseFragment(), CheckboxItemAdapter.CheckBoxListStatus {
	private var _binding: FilterByCountryBinding? = null
	private val binding get() = _binding!!

	private var anItemHasBeenSelected = false

	private lateinit var titleTextView: TextView
	private lateinit var saveTextView: TextView
	private lateinit var countryRecyclerView: RecyclerView
	private lateinit var countryListAdapter: CheckboxItemAdapter
	private var filterEnum: FilterForEnum = FilterForEnum.ACTIONS

	private val actionViewModel: ActionViewModel by sharedViewModel()
	private val transactionViewModel: TransactionViewModel by sharedViewModel()

	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View {
		_binding = FilterByCountryBinding.inflate(inflater, container, false)
		updateFilterFor()
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		titleTextView.setOnClickListener { navigateBack() }
		setupRecyclerView()
		setupSaveFilterClick()
		observeCountryList()
	}

	private fun updateFilterFor() {
		arguments?.let {
			val filterForInt = it.getInt("filterfor", 0)
			if (filterForInt == 1) {
				filterEnum = FilterForEnum.TRANSACTIONS
			}
		}
	}

	private fun initViews() {
		titleTextView = binding.countryTitle
		saveTextView = binding.filterSaveId
		saveTextView.deactivateView()
		countryRecyclerView = binding.filterRecyclerView
	}

	private fun setupRecyclerView() {
		countryRecyclerView.setLayoutManagerToLinear()
		countryRecyclerView.setHasFixedSize(true)
	}

	private fun setupSaveFilterClick() {
		saveTextView.setOnClickListener {
			val selectedCountries = countryListAdapter.getCheckedItemTitles()
			setFilterDataToAppropriateViewModel(selectedCountries)
			navigateBack()
		}
	}

	private fun setFilterDataToAppropriateViewModel(selectedCountries: List<String>) {
		if (filterEnum.isForActions()) actionViewModel.filter_UpdateCountryNameList(
			selectedCountries)
		else if (filterEnum.isForTransactions()) transactionViewModel.filter_UpdateCountryNameList(
			selectedCountries)
	}

	private fun observeCountryList() {
		actionViewModel.loadDistinctCountries()
		actionViewModel.countryListMutableLiveData.observe(viewLifecycleOwner) { allCountries ->
			if (allCountries != null) {
				val checkBoxItems = CheckBoxItem.toList(allCountries, getAlreadyCheckedCountries())
				setCountryListAdapter(checkBoxItems)
			}
		}
	}

	private fun getAlreadyCheckedCountries(): List<String> {
		return if (filterEnum.isForActions())  actionViewModel.getActionFilterParam().networkNameList
		else transactionViewModel.getTransactionFilterParam().networkNameList
	}

	private fun setCountryListAdapter(checkBoxItems: List<CheckBoxItem>) {
		countryListAdapter = CheckboxItemAdapter(checkBoxItems, this)
		countryRecyclerView.adapter = countryListAdapter
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun anItemSelected() {
		if (!anItemHasBeenSelected) {
			anItemHasBeenSelected = true
			saveTextView.activateView()
		}
	}
}