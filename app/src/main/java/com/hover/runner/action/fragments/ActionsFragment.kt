package com.hover.runner.action.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hover.runner.ApplicationInstance
import com.hover.runner.R
import com.hover.runner.action.adapters.ActionRecyclerAdapter
import com.hover.runner.action.listeners.ActionClickListener
import com.hover.runner.action.models.Action
import com.hover.runner.action.navigation.ActionNavigationInterface
import com.hover.runner.action.repo.ActionIdsInANetworkRepo
import com.hover.runner.action.utils.UpdateHoverActions
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.databinding.FragmentActionsBinding
import com.hover.runner.home.SDKCallerInterface
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class ActionsFragment : Fragment(), Hover.DownloadListener, ActionClickListener {

	private var _binding: FragmentActionsBinding? = null
	private val actionViewModel: ActionViewModel by sharedViewModel()

	private lateinit var progressBar: ProgressBar
	private lateinit var actionsRecyclerView: RecyclerView
	private lateinit var pullToRefresh: SwipeRefreshLayout

	private lateinit var emptyInfoLayout: LinearLayout
	private lateinit var emptyStateView: RelativeLayout
	private lateinit var emptyTitle: TextView
	private lateinit var emptySubtitle: TextView
	private lateinit var testAllActionsText: TextView
	private lateinit var filterTextView: TextView

	private var actionRecyclerAdapter: ActionRecyclerAdapter? = null
	private lateinit var actionNavigationInterface: ActionNavigationInterface
	private lateinit var sdkCallerInterface: SDKCallerInterface

	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View {
		_binding = FragmentActionsBinding.inflate(inflater, container, false)
		initNavigationInterface()
		initSDKCallerInterface()
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		showLoadingView()
		pullToRefresh.isRefreshing = false
		setupRecyclerView()
		setupPullToRefresh()
		observeLoadingStatus()
		observeActions()
		setupTestAll()
		handleFilterText()
	}

	private fun initNavigationInterface() {
		actionNavigationInterface = activity as ActionNavigationInterface
	}

	private fun initSDKCallerInterface() {
		sdkCallerInterface = activity as SDKCallerInterface
	}

	override fun onResume() {
		super.onResume()
		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).DARK)
	}

	private fun initViews() {
		progressBar = binding.recyclerViewState.progressState1
		emptyInfoLayout = binding.recyclerViewState.emptyInfoLayout
		emptyStateView = binding.recyclerViewState.layoutForEmptyStateId
		emptyTitle = binding.recyclerViewState.emptyTitleText
		emptySubtitle = binding.recyclerViewState.emptySubtitleText

		pullToRefresh = binding.pullToRefresh
		actionsRecyclerView = binding.recyclerView.recyclerViewId
		testAllActionsText = binding.testAllActionsId
		filterTextView = binding.actionFilterId


	}

	private fun handleFilterText() {
		filterTextView.underline()
		filterTextView.setOnClickListener { actionNavigationInterface.navActionFilterFragment() }
	}

	private fun observeLoadingStatus() {
		actionViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { hasLoaded ->
			if (hasLoaded) showRecyclerView() else showLoadingView()
		}
	}

	private fun updateFilterTextStyle(currentActionListSize: Int) {
		val initialActionListSize: Int = actionViewModel.getHighestActionsCount()
		val isFilterOn: Boolean = currentActionListSize < initialActionListSize

		if (isFilterOn) filterTextView.styleAsFilterOn()
		else filterTextView.styleAsFilterOff()
	}

	private fun updateActionIdsInNetworkIfRequired(actions: List<Action>) {
		if(!ApplicationInstance.cacheForActionIdsInNetworkRepoIsAvailable) {
			GlobalScope.launch(Dispatchers.IO) {
				ActionIdsInANetworkRepo.cache(actions, requireContext())
			}
			ApplicationInstance.cacheForActionIdsInNetworkRepoIsAvailable = true
		}
	}

	private fun observeActions() {
		actionViewModel.actions.observe(viewLifecycleOwner) { actions ->
			if (actions != null) {
				updateFilterTextStyle(actions.size)
				updateActionIdsInNetworkIfRequired(actions)

				if (actions.isEmpty()) showEmptyDataView()
				else setActionListAdapter(actions)
			}
			else showLoadingView()
		}
	}

	private fun setActionListAdapter(actions: List<Action>) {
		fun setAdapter() {
			actionRecyclerAdapter = ActionRecyclerAdapter(actions, this)
			actionsRecyclerView.adapter = actionRecyclerAdapter
		}
		when {
			actionRecyclerAdapter == null -> setAdapter()
			actionRecyclerAdapter!!.currentList != actions -> setAdapter()
		}
	}

	private fun setupTestAll() {
		testAllActionsText.setSafeOnClickListener {
			with(actionViewModel) {
				when {
					hasBadActions() -> actionNavigationInterface.navUnCompletedVariableFragment()
					getRunnableActions().isNotEmpty() -> sdkCallerInterface.runChainedActions()
					else -> UIHelper.flashMessage(requireContext(),
					                              resources.getString(R.string.noRunnableAction))
				}
			}
		}
	}

	private fun showLoadingView() {
		actionsRecyclerView.visibility = View.GONE
		progressBar.visibility = View.VISIBLE

		emptyInfoLayout.visibility = View.GONE
		emptyStateView.visibility = View.GONE
	}

	private fun showRecyclerView() {
		progressBar.visibility = View.GONE
		actionsRecyclerView.visibility = View.VISIBLE
		emptyInfoLayout.visibility = View.GONE
		emptyStateView.visibility = View.GONE
	}

	private fun showEmptyDataView() {
		actionsRecyclerView.visibility = View.GONE
		progressBar.visibility = View.GONE
		emptyInfoLayout.visibility = View.VISIBLE
		emptyStateView.visibility = View.VISIBLE

		emptyTitle.text = resources.getString(R.string.no_actions_yet)
		emptySubtitle.text = resources.getString(R.string.no_actions_desc)
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
		actionViewModel.getAllActions()
		UIHelper.flashMessage(requireContext(),
		                      resources.getString(R.string.refreshed_successfully))
	}

	override fun onActionItemClick(actionId: String, titleTextView: View) {
		actionNavigationInterface.navActionDetails(actionId, titleTextView)
	}
}