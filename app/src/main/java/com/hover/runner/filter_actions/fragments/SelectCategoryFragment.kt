package com.hover.runner.filter_actions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.FilterByCategoriesBinding
import com.hover.runner.filter_actions.adapters.CheckboxItemAdapter
import com.hover.runner.filter_actions.model.ActionFilterParam
import com.hover.runner.filter_actions.model.CheckBoxItem
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.TextViewUtils.Companion.activateView
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SelectCategoryFragment : BaseFragment(), CheckboxItemAdapter.CheckBoxListStatus {
    private var _binding: FilterByCategoriesBinding? = null
    private val binding get() = _binding!!

    private var anItemWasSelected = false
    private lateinit var titleTextView : TextView
    private lateinit var saveTextView  : TextView
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryListAdapter : CheckboxItemAdapter

    private val actionViewModel : ActionViewModel by sharedViewModel()
    private val transactionViewModel : TransactionViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FilterByCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        titleTextView.setOnClickListener { navigateBack() }
        setupSaveFilterClick()
        setupRecyclerView()
        observeCategoryList()

    }
    private fun initViews() {
        titleTextView = binding.categoryTitle
        saveTextView = binding.filterSaveId
        saveTextView.isClickable = false
        categoryRecyclerView = binding.filterRecyclerView
    }

    private fun setupSaveFilterClick() {
        val selectedCategories = categoryListAdapter.getCheckedItems()
        saveTextView.setOnClickListener { actionViewModel.filter_UpdateCategoryList(selectedCategories) }
        navigateBack()
    }

    private fun setupRecyclerView() {
        categoryRecyclerView.setLayoutManagerToLinear()
        categoryRecyclerView.setHasFixedSize(true)
    }

    private fun observeCategoryList()  {
        transactionViewModel.loadDistinctCategories()
        transactionViewModel.distinctCategoryMutableLiveData.observe(viewLifecycleOwner) { allCategories->
            if(allCategories!=null) {
                val filterParam : ActionFilterParam = actionViewModel.filter_getParam
                val checkBoxItems = CheckBoxItem.toList(allCategories, filterParam.categoryList)

                setCategoryListAdapter(checkBoxItems)
            }
        }
    }


    private fun setCategoryListAdapter(checkBoxItems : List<CheckBoxItem>) {
        categoryListAdapter = CheckboxItemAdapter(checkBoxItems, this)
        categoryRecyclerView.adapter = categoryListAdapter
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