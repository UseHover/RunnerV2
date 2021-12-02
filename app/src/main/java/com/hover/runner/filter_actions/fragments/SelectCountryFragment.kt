package com.hover.runner.filter_actions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.FilterByCountryBinding
import com.hover.runner.filter_actions.adapters.CheckboxItemAdapter
import com.hover.runner.filter_actions.model.ActionFilterParam
import com.hover.runner.filter_actions.model.CheckBoxItem
import com.hover.runner.utils.TextViewUtils.Companion.activateView
import com.hover.runner.utils.TextViewUtils.Companion.deactivateView
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SelectCountryFragment : BaseFragment(), CheckboxItemAdapter.CheckBoxListStatus {
    private var _binding: FilterByCountryBinding? = null
    private val binding get() = _binding!!

    private var anItemWasSelected = false

    private lateinit var titleTextView : TextView
    private lateinit var saveTextView : TextView
    private lateinit var countryRecyclerView : RecyclerView
    private lateinit var countryListAdapter : CheckboxItemAdapter

    private val actionViewModel : ActionViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FilterByCountryBinding.inflate(inflater, container, false)
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
            val selectedCountries = countryListAdapter.getCheckedItems()
            actionViewModel.filter_UpdateCountryNameList(selectedCountries)
            navigateBack()
        }
    }

    private fun observeCountryList()  {
        actionViewModel.loadDistinctCountries()
        actionViewModel.countryListMutableLiveData.observe(viewLifecycleOwner) { allCountries ->
            if(allCountries !=null) {
                val filterParam : ActionFilterParam? = actionViewModel.filter_getParam
                if(filterParam !=null) {
                    val checkBoxItems = CheckBoxItem.toList(allCountries, filterParam.countryNameList)
                    setCountryListAdapter(checkBoxItems)
                }
            }
        }
    }
    private fun setCountryListAdapter(checkBoxItems : List<CheckBoxItem>) {
        countryListAdapter = CheckboxItemAdapter(checkBoxItems, this)
        countryRecyclerView.adapter = countryListAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun anItemSelected() {
        if(!anItemWasSelected) {
            anItemWasSelected = true
            saveTextView.activateView()
        }
    }
}