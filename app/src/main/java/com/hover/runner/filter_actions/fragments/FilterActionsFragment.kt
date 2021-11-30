package com.hover.runner.filter_actions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.fragment.app.Fragment
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.databinding.ActionFilterFragmentBinding
import com.hover.runner.filter_actions.navigation.FilterActionNavigationInterface
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class FilterActionsFragment : Fragment() {
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
    private var resetActivated = false
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
        showActionsTextView.setOnClickListener { navigateBack() }
        UIHelper.underlineText(resetTextView, "Reset")
    }
    private fun setupFilterSelections() {
        setupFilterEntries()
        setupCheckboxClicks()
    }
    private fun setupFilterEntries() {

    }

    private fun setupCheckboxClicks() {
        successCheckBox.setOnCheckedChangeListener { _, checked: Boolean -> actionViewModel.filter_IncludeSucceededTransactions(checked) }
        pendingCheckBox.setOnCheckedChangeListener{_, checked-> actionViewModel.filter_IncludePendingTransactions(checked) }
        failureCheckBox.setOnCheckedChangeListener{_, checked-> actionViewModel.filter_IncludeFailedTransactions(checked) }
        noTransactionCheckBox.setOnCheckedChangeListener{_, checked -> actionViewModel.filter_IncludeActionsWithNoTransaction(checked) }
        hasParserCheckBox.setOnCheckedChangeListener{ _, checked -> actionViewModel.filter_IncludeActionsWithParsers(checked) }
        onlyWithSimPresentCheckBox.setOnCheckedChangeListener{_, checked -> actionViewModel.filter_ShowOnlyActionsWithSimPresent(checked) }
    }

    private fun navigateBack() {
        requireActivity().finish()
    }


    private fun deactivateReset() {
        resetTextView.setTextColor(RunnerColor(requireContext()).GRAY)
        resetActivated = false
    }

    private fun activateReset() {
        resetTextView.setTextColor(RunnerColor(requireContext()).WHITE)
        resetActivated = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}