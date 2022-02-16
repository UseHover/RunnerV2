package com.hover.runner.actionFilters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hover.runner.actions.ActionsViewModel
import com.hover.runner.home.BaseFragment
import com.hover.runner.databinding.FragmentSelectionBinding
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class SelectionFragment : BaseFragment() {
	private val actionsViewModel: ActionsViewModel by sharedViewModel()

	private var _binding: FragmentSelectionBinding? = null
	private val binding get() = _binding!!

	private lateinit var itemAdapter: SelectionAdapter

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentSelectionBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.selectionTitle.setOnClickListener { navigateBack() }
		setupSave()
		setupList()
	}

	private fun setupSave() {
		binding.selectionSaveBtn.setOnClickListener {
			Timber.e(itemAdapter.selectedItems.toString())
			actionsViewModel.setTags(itemAdapter.selectedItems)
			navigateBack()
		}
	}

	private fun setupList() {
		binding.selectionRecycler.setLayoutManagerToLinear()
		actionsViewModel.getAllTags().observe(viewLifecycleOwner) {
			it?.let {
				itemAdapter = SelectionAdapter(it, actionsViewModel.selectedTags.value!!.toMutableList())
				binding.selectionRecycler.adapter = itemAdapter
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}