package com.hover.runner.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hover.runner.ApplicationInstance
import com.hover.runner.R
import com.hover.runner.databinding.FragmentActionsBinding
import com.hover.runner.utils.NetworkUtil
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.TextViewUtils.Companion.styleAsFilterOff
import com.hover.runner.utils.TextViewUtils.Companion.styleAsFilterOn
import com.hover.runner.utils.TextViewUtils.Companion.underline
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.runner.utils.setSafeOnClickListener
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class ActionsFragment : Fragment(), Hover.DownloadListener, ActionRecyclerAdapter.ActionClickListener {

	private var _binding: FragmentActionsBinding? = null
	private val actionsViewModel: ActionsViewModel by sharedViewModel()

	private lateinit var actionsRecyclerView: RecyclerView
	private lateinit var pullToRefresh: SwipeRefreshLayout
	private lateinit var filterTextView: TextView

	private var actionRecyclerAdapter: ActionRecyclerAdapter? = null

	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentActionsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		showLoadingView()
		pullToRefresh.isRefreshing = false
		setupRecyclerView()
		setupPullToRefresh()
		observeActions()
		setupTestAll()
		handleFilterText()
	}

	override fun onResume() {
		super.onResume()
		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).DARK)
	}

	private fun initViews() {
		pullToRefresh = binding.pullToRefresh
		actionsRecyclerView = binding.recyclerView
		filterTextView = binding.actionFilterId
	}

	private fun handleFilterText() {
		filterTextView.underline()
		filterTextView.setOnClickListener { view -> view.findNavController().navigate(R.id.navigation_actionFilter) }
	}

	private fun updateFilterTextStyle(currentActionListSize: Int) {
		val isFilterOn: Boolean = currentActionListSize < actionsViewModel.allActions.value!!.size

		if (isFilterOn) filterTextView.styleAsFilterOn()
		else filterTextView.styleAsFilterOff()
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
		actionRecyclerAdapter = ActionRecyclerAdapter(actions, this)
		actionsRecyclerView.adapter = actionRecyclerAdapter
	}

	private fun setupTestAll() {
		binding.testAllActionsId.setSafeOnClickListener {
			if (!actionsViewModel.filteredActions.value.isNullOrEmpty())
				it.findNavController().navigate(R.id.navigation_uncompletedVariableFragment)
			else
				UIHelper.flashMessage(requireContext(), resources.getString(R.string.noRunnableAction))
		}
	}

	private fun showLoadingView() {
		binding.recyclerViewState.progressState1.visibility = View.VISIBLE
	}

	private fun setupRecyclerView() {
		actionsRecyclerView.setLayoutManagerToLinear()
		actionsRecyclerView.setHasFixedSize(true)
	}

	private fun setupPullToRefresh() {
		pullToRefresh.setOnRefreshListener {
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
		pullToRefresh.isRefreshing = false
		UIHelper.flashMessage(requireContext(),
		                      if (activity != null) requireActivity().currentFocus else null,
		                      requireContext().getString(R.string.NO_NETWORK))
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onError(reason: String?) {
		pullToRefresh.isRefreshing = false
		UIHelper.flashMessage(requireContext(), reason)
	}

	override fun onSuccess(actionList: ArrayList<HoverAction>?) {
		pullToRefresh.isRefreshing = false
		UIHelper.flashMessage(requireContext(), resources.getString(R.string.refreshed_successfully))
	}

	override fun onActionItemClick(actionId: String, titleTextView: View) {
		val bundle = bundleOf("action_id" to actionId)
		findNavController().navigate(R.id.navigation_actionDetails, bundle)
	}
}