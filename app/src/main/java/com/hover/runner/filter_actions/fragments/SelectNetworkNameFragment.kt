package com.hover.runner.filter_actions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hover.runner.base.fragment.BaseFragment
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.databinding.FilterByNetworkNameBinding
import com.hover.runner.filter_actions.adapters.CheckboxItemAdapter
import com.hover.runner.filter_actions.model.ActionFilterParam
import com.hover.runner.filter_actions.model.CheckBoxItem
import com.hover.runner.utils.TextViewUtils.Companion.activateView
import com.hover.runner.utils.TextViewUtils.Companion.deactivateView
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.MessageFormat

class SelectNetworkNameFragment : BaseFragment(), CheckboxItemAdapter.CheckBoxListStatus {
    private var _binding: FilterByNetworkNameBinding? = null
    private val binding get() = _binding!!

    private var anItemWasSelected = false

    private lateinit var titleTextView : TextView
    private lateinit var saveTextView : TextView
    private lateinit var networksInOtherCountriesTextView: TextView
    private lateinit var networksInPresentSimCountryRecyclerView : RecyclerView
    private lateinit var networksOutsidePresentSimCountryRecyclerView : RecyclerView
    private lateinit var networksInPresentSimCountryListAdapter : CheckboxItemAdapter
    private lateinit var networksOutsidePresentSimCountryListAdapter : CheckboxItemAdapter

    private val actionViewModel : ActionViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FilterByNetworkNameBinding.inflate(inflater, container, false)
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

    private fun initViews() {
        titleTextView = binding. networksTitle
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
            val selectedNetworksWithinCountry = networksInPresentSimCountryListAdapter.getCheckedItems()
            val selectedNetworksOutsideCountry = networksOutsidePresentSimCountryListAdapter.getCheckedItems()
            val totalList : List<String> = selectedNetworksWithinCountry + selectedNetworksOutsideCountry

            actionViewModel.filter_UpdateNetworkNameList(totalList)
            navigateBack()
        }
    }

    private fun observeNetworkLists() {
        actionViewModel.loadNetworkNames()
        observeNetworksInPresentSimCountry()
        observeNetworkOutsidePresentSimCountry()
    }
    private fun observeNetworksInPresentSimCountry()  {
        actionViewModel.networksInPresentSimCountryNamesLiveData.observe(viewLifecycleOwner) { allNetworks ->
            if(allNetworks !=null) {
                val filterParam : ActionFilterParam? = actionViewModel.filter_getParam
                if(filterParam !=null) {
                    val checkBoxItems =
                        CheckBoxItem.toList(allNetworks, filterParam.networkNameList)
                       setInPresentSimCountryListAdapter(checkBoxItems)
                }
            }
        }
    }

    private fun observeNetworkOutsidePresentSimCountry()  {
        actionViewModel.networksInPresentSimCountryNamesLiveData.observe(viewLifecycleOwner) { allNetworks ->
            if(allNetworks !=null) {
                val filterParam : ActionFilterParam? = actionViewModel.filter_getParam
                if(filterParam !=null) {
                    val checkBoxItems =
                        CheckBoxItem.toList(allNetworks, filterParam.networkNameList)
                        setOutsidePresentSimCountryListAdapter(checkBoxItems)
                        setOtherCountryVisibility(View.VISIBLE)
                        networksInOtherCountriesTextView.text = MessageFormat.format("+ {0} in other countries", allNetworks.size)
                }
            }
            else {
                setOtherCountryVisibility(View.GONE)
            }
        }
    }
    private fun setOtherCountryVisibility(visibility : Int) {
        networksInOtherCountriesTextView.visibility = visibility
        networksInPresentSimCountryRecyclerView.visibility = visibility
    }
    private fun setInPresentSimCountryListAdapter(checkBoxItems : List<CheckBoxItem>) {
        networksInPresentSimCountryListAdapter = CheckboxItemAdapter(checkBoxItems, this)
        networksInPresentSimCountryRecyclerView.adapter = networksInPresentSimCountryListAdapter
    }

    private fun setOutsidePresentSimCountryListAdapter(checkBoxItems : List<CheckBoxItem>) {
        networksOutsidePresentSimCountryListAdapter = CheckboxItemAdapter(checkBoxItems, this)
        networksOutsidePresentSimCountryRecyclerView.adapter = networksOutsidePresentSimCountryListAdapter
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