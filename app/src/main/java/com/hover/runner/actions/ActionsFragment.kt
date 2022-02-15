package com.hover.runner.actions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.hover.runner.ApplicationInstance
import com.hover.runner.R
import com.hover.runner.database.UpdateHoverActions
import com.hover.runner.databinding.FragmentActionsBinding
import com.hover.runner.utils.*
import com.hover.runner.utils.TextViewUtils.Companion.styleAsFilterOff
import com.hover.runner.utils.TextViewUtils.Companion.styleAsFilterOn
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class ActionsFragment : Fragment(), Hover.DownloadListener, ActionRecyclerAdapter.ActionClickListener {

	private var _binding: FragmentActionsBinding? = null
	private val actionsViewModel: ActionsViewModel by sharedViewModel()
	private var actionRecyclerAdapter: ActionRecyclerAdapter? = null
	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentActionsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		showLoadingView()
		observeActions()
		setupListeners()
	}

	override fun onResume() {
		super.onResume()
		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).DARK)
	}

	private fun updateFilterTextStyle(currentActionListSize: Int) {
		val isFilterOn: Boolean = currentActionListSize < actionsViewModel.allActions.value!!.size

		if (isFilterOn) binding.actionFilter.styleAsFilterOn()
		else binding.actionFilter.styleAsFilterOff()
	}

	private fun observeActions() {
		actionsViewModel.filteredActions.observe(viewLifecycleOwner) { actions ->
			if (actions.isNullOrEmpty()) showLoadingView()
			else {
				updateFilterTextStyle(actions.size)
				setActionListAdapter(actions)
			}
		}
	}

	private fun setActionListAdapter(actions: List<HoverAction>) {
		binding.recyclerView.setLayoutManagerToLinear()
		actionRecyclerAdapter = ActionRecyclerAdapter(actions, this)
		binding.recyclerView.adapter = actionRecyclerAdapter
	}

	private fun setupListeners() {
		setupPullToRefresh()
		binding.actionFilter.setOnClickListener { view -> view.findNavController().navigate(R.id.navigation_actionFilter) }
		binding.testAllActionsId.setSafeOnClickListener { navigateToRun(); }
	}

	private fun navigateToRun() {
		if (!actionsViewModel.filteredActions.value.isNullOrEmpty()) {
			findNavController().navigate(R.id.navigation_run_variables,
				bundleOf(Pair("action_ids",
					Utils.convertActionListToIds(actionsViewModel.filteredActions.value!!).toTypedArray())))
		} else
			UIHelper.flashMessage(requireContext(), resources.getString(R.string.noRunnableAction))
	}

	private fun showLoadingView() {
		binding.pullToRefresh.isRefreshing = false
		binding.recyclerViewState.progressState1.visibility = View.VISIBLE
	}

	private fun setupPullToRefresh() {
		binding.pullToRefresh.setOnRefreshListener {
			if (NetworkUtil(requireContext()).isNetworkAvailable) refreshActions()
			else showNetworkError()
		}
	}

	private fun refreshActions() {
		ApplicationInstance.cacheForActionIdsInNetworkRepoIsAvailable = false
		val updateHoverActions = UpdateHoverActions(this, requireContext())
		updateHoverActions.init()
	}

	private fun showNetworkError() {
		binding.pullToRefresh.isRefreshing = false
		UIHelper.flashMessage(requireContext(),
		                      if (activity != null) requireActivity().currentFocus else null,
		                      requireContext().getString(R.string.NO_NETWORK))
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onError(reason: String?) {
		binding.pullToRefresh.isRefreshing = false
		UIHelper.flashMessage(requireContext(), reason)
	}

	override fun onSuccess(actionList: ArrayList<HoverAction>?) {
		binding.pullToRefresh.isRefreshing = false
		UIHelper.flashMessage(requireContext(), resources.getString(R.string.refreshed_successfully))
	}

	override fun onActionItemClick(actionId: String, titleTextView: View) {
		val bundle = bundleOf("action_id" to actionId)
		findNavController().navigate(R.id.navigation_actionDetails, bundle)
	}
}