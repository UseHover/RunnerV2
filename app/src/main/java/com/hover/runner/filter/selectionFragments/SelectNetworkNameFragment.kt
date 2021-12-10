package com.hover.runner.filter.selectionFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.FilterByNetworkNameBinding
import com.hover.runner.filter.checkbox.CheckBoxItem
import com.hover.runner.filter.checkbox.CheckboxItemAdapter
import com.hover.runner.filter.enumValue.FilterForEnum
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.TextViewUtils.Companion.activateView
import com.hover.runner.utils.TextViewUtils.Companion.deactivateView
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.MessageFormat

class SelectNetworkNameFragment : BaseFragment(), CheckboxItemAdapter.CheckBoxListStatus {
	private var _binding: FilterByNetworkNameBinding? = null
	private val binding get() = _binding!!

	private var anItemWasSelected = false

	private lateinit var titleTextView: TextView
	private lateinit var saveTextView: TextView
	private lateinit var networksInOtherCountriesTextView: TextView
	private lateinit var networksInPresentSimCountryRecyclerView: RecyclerView
	private lateinit var networksOutsidePresentSimCountryRecyclerView: RecyclerView
	private lateinit var networksInPresentSimCountryListAdapter: CheckboxItemAdapter
	private lateinit var networksOutsidePresentSimCountryListAdapter: CheckboxItemAdapter

	private val actionViewModel: ActionViewModel by sharedViewModel()
	private val transactionViewModel: TransactionViewModel by sharedViewModel()

	private var filterEnum: FilterForEnum = FilterForEnum.ACTIONS

	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View {
		_binding = FilterByNetworkNameBinding.inflate(inflater, container, false)
		updateFilterFor()
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		titleTextView.setOnClickListener { navigateBack() }
		setupRecyclerViews()
		observeNetworkLists()
		setupSaveFilterClick()
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
		titleTextView = binding.networksTitle
		saveTextView = binding.filterSaveId
		networksInOtherCountriesTextView = binding.networksInOtherCountries
		saveTextView.deactivateView()
		networksInPresentSimCountryRecyclerView = binding.filterRecyclerView1
		networksOutsidePresentSimCountryRecyclerView = binding.filterRecyclerView2
	}

	private fun setupRecyclerViews() {
		networksInPresentSimCountryRecyclerView.setLayoutManagerToLinear()
		networksInPresentSimCountryRecyclerView.setHasFixedSize(true)

		networksOutsidePresentSimCountryRecyclerView.setLayoutManagerToLinear()
		networksOutsidePresentSimCountryRecyclerView.setHasFixedSize(true)
	}

	private fun setupSaveFilterClick() {
		saveTextView.setOnClickListener {
			val selectedNetworksWithinCountry =
				networksInPresentSimCountryListAdapter.getCheckedItemTitles()
			val selectedNetworksOutsideCountry =
				networksOutsidePresentSimCountryListAdapter.getCheckedItemTitles()
			val totalList: List<String> =
				selectedNetworksWithinCountry + selectedNetworksOutsideCountry

			setFilterDataToAppropriateViewModel(totalList)
			navigateBack()
		}
	}

	private fun setFilterDataToAppropriateViewModel(totalList: List<String>) {
		if (filterEnum.isForActions()) actionViewModel.filter_UpdateNetworkNameList(totalList)
		else if (filterEnum.isForTransactions()) transactionViewModel.filter_UpdateNetworkNameList(
			totalList)
	}

	private fun observeNetworkLists() {
		actionViewModel.loadNetworkNames()
		observeNetworksInPresentSimCountry()
		observeNetworkOutsidePresentSimCountry()
	}

	private fun getAlreadySelectedNetworkNames(): List<String> {
		return if (filterEnum.isForActions()) actionViewModel.filter_getParameters!!.networkNameList
		else transactionViewModel.filter_getParameters!!.networkNameList
	}

	private fun observeNetworksInPresentSimCountry() {
		actionViewModel.networksInPresentSimCountryNamesLiveData.observe(viewLifecycleOwner) { allNetworks ->
			if (allNetworks != null) {
				val checkBoxItems =
					CheckBoxItem.toList(allNetworks, getAlreadySelectedNetworkNames())
				setInPresentSimCountryListAdapter(checkBoxItems)
			}
		}
	}

	private fun observeNetworkOutsidePresentSimCountry() {
		actionViewModel.networksInPresentSimCountryNamesLiveData.observe(viewLifecycleOwner) { allNetworks ->
			if (allNetworks != null) {
				val checkBoxItems =
					CheckBoxItem.toList(allNetworks, getAlreadySelectedNetworkNames())
				setOutsidePresentSimCountryListAdapter(checkBoxItems)
				setOtherCountryVisibility(View.VISIBLE)
				networksInOtherCountriesTextView.text =
					MessageFormat.format("+ {0} in other countries", allNetworks.size)
			}
			else {
				setOtherCountryVisibility(View.GONE)
			}
		}
	}

	private fun setOtherCountryVisibility(visibility: Int) {
		networksInOtherCountriesTextView.visibility = visibility
		networksInPresentSimCountryRecyclerView.visibility = visibility
	}

	private fun setInPresentSimCountryListAdapter(checkBoxItems: List<CheckBoxItem>) {
		networksInPresentSimCountryListAdapter = CheckboxItemAdapter(checkBoxItems, this)
		networksInPresentSimCountryRecyclerView.adapter = networksInPresentSimCountryListAdapter
	}

	private fun setOutsidePresentSimCountryListAdapter(checkBoxItems: List<CheckBoxItem>) {
		networksOutsidePresentSimCountryListAdapter = CheckboxItemAdapter(checkBoxItems, this)
		networksOutsidePresentSimCountryRecyclerView.adapter =
			networksOutsidePresentSimCountryListAdapter
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun anItemSelected() {
		if (!anItemWasSelected) {
			anItemWasSelected = true
			saveTextView.activateView()
		}
	}
}