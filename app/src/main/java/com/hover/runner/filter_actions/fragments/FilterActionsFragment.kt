package com.hover.runner.filter_actions.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.util.Pair
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.hover.runner.R
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.ActionFilterFragmentBinding
import com.hover.runner.filter_actions.model.ActionFilterParam
import com.hover.runner.filter_actions.navigation.FilterActionNavigationInterface
import com.hover.runner.utils.TextViewUtils.Companion.activateView
import com.hover.runner.utils.TextViewUtils.Companion.deactivateView
import com.hover.runner.utils.TextViewUtils.Companion.underline
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class FilterActionsFragment : BaseFragment() {
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var resetTextView: TextView
    private  lateinit var countryEntryTextView: TextView
    private  lateinit var networkEntryTextView:TextView
    private  lateinit var categoryEntryTextView:TextView
    private  lateinit var datePickerTextView:TextView
    private lateinit var searchActionEditText: EditText
    private lateinit var successCheckBox: AppCompatCheckBox
    private  lateinit var pendingCheckBox: AppCompatCheckBox
    private  lateinit var failureCheckBox:AppCompatCheckBox
    private  lateinit var noTransactionCheckBox: AppCompatCheckBox
    private  lateinit var hasParserCheckBox:AppCompatCheckBox
    private  lateinit var onlyWithSimPresentCheckBox:AppCompatCheckBox
    private lateinit var toolBarTextView: TextView
    private lateinit var filterLayout : LinearLayout
    private lateinit var showActionsTextView : TextView

    private var timer = Timer()
    private val actionViewModel: ActionViewModel by sharedViewModel()

    private lateinit var filterActionNavigationInterface : FilterActionNavigationInterface

    private var _binding: ActionFilterFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActionFilterFragmentBinding.inflate(inflater, container, false)
        initNavigationInterface()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupToolBarViews()
        setupFilterSelections()
        setupResetFilter()
        observeFilterParameters()
        setupShowFilteredActions()
        observeFilterActions()
    }

    private fun initNavigationInterface() {
        filterActionNavigationInterface = activity as FilterActionNavigationInterface
    }

    private fun initViews(){
        toolBarTextView = binding.actionFilterBackId
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
    private fun setupToolBarViews() {
        toolBarTextView.setOnClickListener { navigateBack() }
        resetTextView.underline()
    }
    private fun setupShowFilteredActions() {
        showActionsTextView.setOnClickListener {
            val filteredActions = actionViewModel.filter_getActions()
            actionViewModel.updateActionsLiveData(filteredActions)
            navigateBack()
        }
    }
    private fun setupFilterSelections() {
        setupFilterEntryBoxSelections()
        setupCheckboxSelections()
    }

    private fun observeFilterParameters() {
     actionViewModel.actionFilterParamMutableLiveData.observe(viewLifecycleOwner) {
         updateFilterEntryData(it)
         updateFilterCheckboxes(it)
     }
    }

    private fun observeFilterActions() {
        observeFilterData()
        observeFilterLoadingStatus()
    }
    private fun observeFilterData() {
        actionViewModel.filteredActionsMutableLiveData.observe(viewLifecycleOwner) { actions ->
            if (actions != null) {
                with(showActionsTextView) {
                    if (actions.isNotEmpty()) {
                        isClickable = true
                        setBackgroundColor(resources.getColor(R.color.colorPrimary))
                        val suffixAction = if (actions.size == 1) "action " else  "action"
                        text = String.format(Locale.getDefault(), "Show %d %s", actions.size, suffixAction)
                    }
                    else {
                    isClickable = false
                    setBackgroundColor(resources.getColor(R.color.colorMainGrey))
                    text = resources.getString(R.string.no_actions_filter_result)
                }
            }
        }
        }
    }
    private fun observeFilterLoadingStatus() {
        actionViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { hasLoaded ->
            if(hasLoaded) {
                with(showActionsTextView) {
                    isClickable = false
                    setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    text = resources.getString(R.string.loadingText)
                }
            }
        }
    }

    private fun setupResetFilter() {
        handleResetTextClick()
        observeForResetTextView()
    }

    private fun handleResetTextClick() {
        actionViewModel.getAllActions()
        actionViewModel.filter_reset()
    }
    private fun observeForResetTextView() {
        actionViewModel.actionsParentTotalLiveData.observe(viewLifecycleOwner) {
            if(actionViewModel.filter_actionsTotal() < it) activateReset()
            else deactivateReset()
        }
    }

    private fun updateFilterEntryData(param: ActionFilterParam) {
        with(param) {
            searchActionEditText.setText(getActionIdsAsString())
            countryEntryTextView.text = getCountryListAsString()
            categoryEntryTextView.text = getCategoryListAsString()
            networkEntryTextView.text = getNetworkNamesAsString()
            datePickerTextView.text = getDateRangeValue(requireContext())
        }
    }

    private fun updateFilterCheckboxes(param: ActionFilterParam) {
        with(param) {
            successCheckBox.isChecked = isTransactionSuccessfulIncluded()
            pendingCheckBox.isChecked = isTransactionPendingIncluded()
            failureCheckBox.isChecked = isTransactionFailedIncluded()
            noTransactionCheckBox.isChecked = hasNoTransaction
            hasParserCheckBox.isChecked = hasParser
            onlyWithSimPresentCheckBox.isChecked = onlyWithSimPresent
        }
    }

    private fun setupCheckboxSelections() {
        successCheckBox.setOnCheckedChangeListener { _, checked: Boolean -> actionViewModel.filter_IncludeSucceededTransactions(checked) }
        pendingCheckBox.setOnCheckedChangeListener{_, checked-> actionViewModel.filter_IncludePendingTransactions(checked) }
        failureCheckBox.setOnCheckedChangeListener{_, checked-> actionViewModel.filter_IncludeFailedTransactions(checked) }
        noTransactionCheckBox.setOnCheckedChangeListener{_, checked -> actionViewModel.filter_IncludeActionsWithNoTransaction(checked) }
        hasParserCheckBox.setOnCheckedChangeListener{ _, checked -> actionViewModel.filter_IncludeActionsWithParsers(checked) }
        onlyWithSimPresentCheckBox.setOnCheckedChangeListener{_, checked -> actionViewModel.filter_ShowOnlyActionsWithSimPresent(checked) }
    }
    private fun setupFilterEntryBoxSelections() {
        searchActionEditText.addTextChangedListener(searchActionEditTextWatcher)
        countryEntryTextView.setOnClickListener{ filterActionNavigationInterface.navigateToFilterByCountriesFragment() }
        categoryEntryTextView.setOnClickListener { filterActionNavigationInterface.navigateToFilterByActionCategoriesFragment() }
        networkEntryTextView.setOnClickListener{ filterActionNavigationInterface.navigateToFilterByNetworksFragment() }
        datePickerTextView.setOnClickListener{ pickDateRange() }
    }

    private fun pickDateRange() {
        with(datePicker()) {
            show(this@FilterActionsFragment.parentFragmentManager, toString())
        }
    }
    private fun datePicker() : MaterialDatePicker<Pair<Long, Long>> {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val constraintsBuilder = CalendarConstraints.Builder()
        builder.setCalendarConstraints(constraintsBuilder.build())

        val picker = builder.setTitleText(resources.getString(R.string.selected_range)).build()
        picker.addOnPositiveButtonClickListener { selection: Pair<Long, Long>? ->
           selection?.let {actionViewModel.filter_UpdateDateRange(it.first, it.second)}
        }
        return picker
    }
    private val searchActionEditTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            timer.cancel()
            timer = Timer()
            val throttle: Long = 1500
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if(s.isNotEmpty()) actionViewModel.filter_byActionSearch(s.toString())
                }
            }, throttle)
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
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