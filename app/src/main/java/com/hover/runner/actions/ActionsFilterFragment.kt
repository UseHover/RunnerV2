package com.hover.runner.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.filters.FilterFragment
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class ActionsFilterFragment : FilterFragment() {
	private lateinit var actionsViewModel: ActionsViewModel

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		viewModel = getSharedViewModel<ActionsViewModel>()
		actionsViewModel = viewModel as ActionsViewModel

		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.filterTitle.text = getString(R.string.title_filter_actions)
		binding.searchInput.hint = getString(R.string.hint_search)
//		binding.dateWrapper.visibility = View.GONE
	}

	override fun onSave() {
		findNavController().navigate(R.id.navigation_actions)
	}

	override fun observeFilterData() {
		super.observeFilterData()
		actionsViewModel.filteredActions.observe(viewLifecycleOwner) { actions ->
			actions?.let { binding.filterNow.text = getString(R.string.cta_filter_actions, actions.size)}
		}
	}
}