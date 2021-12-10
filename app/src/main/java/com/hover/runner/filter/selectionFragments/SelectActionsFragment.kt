package com.hover.runner.filter.selectionFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.FilterByActionsBinding
import com.hover.runner.filter.checkbox.CheckboxItemAdapter
import com.hover.runner.filter.checkbox.CheckBoxItem
import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.TextViewUtils.Companion.activateView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SelectActionsFragment : BaseFragment(), CheckboxItemAdapter.CheckBoxListStatus {
	private var _binding: FilterByActionsBinding? = null
	private val binding get() = _binding!!

	private var anItemHasBeenSelected = false

	private val actionViewModel: ActionViewModel by sharedViewModel()
	private val transactionViewModel: TransactionViewModel by sharedViewModel()

	private lateinit var saveTextView: TextView
	private lateinit var menuTitleTextView: TextView

	private lateinit var actionsRecyclerView: RecyclerView
	private lateinit var actionsRecyclerAdapter: CheckboxItemAdapter

	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View {
		_binding = FilterByActionsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		setupMenuTitleClick()
		setupSaveFilterClick()
		observeActionList()
	}

	private fun setupSaveFilterClick() {
		saveTextView.setOnClickListener {
			val selectedActions = actionsRecyclerAdapter.getCheckedItemSubtitles()
			transactionViewModel.filter_UpdateActionIds(selectedActions)
			navigateBack()
		}
	}

	private fun initViews() {
		saveTextView = binding.filterSaveId
		actionsRecyclerView = binding.filterRecyclerView
		menuTitleTextView = binding.filterByActionTitle
	}

	private fun setupMenuTitleClick() {
		menuTitleTextView.setOnClickListener { navigateBack() }
	}

	private fun observeActionList() {
		actionViewModel.getAllActions()
		actionViewModel.actions.observe(viewLifecycleOwner) { actions ->
			if (actions != null) {
				val transactionFilterParameters: TransactionFilterParameters =
					transactionViewModel.filter_getParameters!!
				val checkBoxItems =
					CheckBoxItem.toListByActions(actions, transactionFilterParameters.actionIdList)
				setCountryListAdapter(checkBoxItems)
			}
		}
	}

	private fun setCountryListAdapter(checkBoxItems: List<CheckBoxItem>) {
		actionsRecyclerAdapter = CheckboxItemAdapter(checkBoxItems, this)
		actionsRecyclerView.adapter = actionsRecyclerAdapter
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