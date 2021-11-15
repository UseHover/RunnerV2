package com.hover.runner.actions.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hover.runner.R
import com.hover.runner.actions.listeners.ActionClickListener
import com.hover.runner.actions.adapters.ActionRecyclerAdapter
import com.hover.runner.actions.viewmodel.ActionViewModel
import com.hover.runner.databinding.FragmentActionsBinding
import com.hover.runner.actions.navigation.ActionNavigationInterface
import com.hover.runner.home.SDKCallerInterface
import com.hover.runner.utils.*
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.Exception
import java.util.ArrayList

class ActionsFragment  : Fragment(),
      Hover.DownloadListener, ActionClickListener {

    private var _binding: FragmentActionsBinding? = null
    private val actionViewModel: ActionViewModel by sharedViewModel()

    private lateinit var progressBar: ProgressBar
    private lateinit var actionsRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout

    private lateinit var emptyInfoLayout: LinearLayout
    private lateinit var emptyStateView: RelativeLayout
    private lateinit var emptyTitle: TextView
    private lateinit var emptySubtitle:TextView
    private lateinit var testAllActionsText : TextView



    private lateinit var actionNavigationInterface : ActionNavigationInterface
    private lateinit var sdkCallerInterface : SDKCallerInterface

    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentActionsBinding.inflate(inflater, container, false)
        initInterfaces()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        pullToRefresh.isRefreshing = false
        setupRecyclerView()
        setupPullToRefresh()
        observeActionLoading()
        observeActions()
        setupTestAll()
    }
    private fun initInterfaces() {
        actionNavigationInterface  = activity as ActionNavigationInterface
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

    }

    private fun observeActionLoading() {
        actionViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { hasLoaded ->
            if(hasLoaded)  showRecyclerView() else showLoadingView()
        }
    }

    private fun observeActions() {
        actionViewModel.getAllActions()
        actionViewModel.actions.observe(viewLifecycleOwner) { actions ->
            if(actions !=null) {
                if(actions.isEmpty()) {
                    showEmptyDataView()
                }
                else {
                    val actionRecyclerAdapter = ActionRecyclerAdapter(actions, this)
                    actionsRecyclerView.adapter = actionRecyclerAdapter
                }
            }
        }
    }

    private fun setupTestAll() {
        testAllActionsText.setSafeOnClickListener {
            with(actionViewModel) {
                when {
                    hasBadActions() -> actionNavigationInterface.navUnCompletedVariableFragment()
                    getRunnableActions().isNotEmpty() -> sdkCallerInterface.runChainedActions()
                    else -> UIHelper.flashMessage(requireContext(), resources.getString(R.string.noRunnableAction))
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
        actionsRecyclerView.layoutManager = UIHelper.setMainLinearManagers(context)
        actionsRecyclerView.setHasFixedSize(true)
    }

    private fun setupPullToRefresh() {
        pullToRefresh.setOnRefreshListener {
            if (NetworkUtil(requireContext()).isNetworkAvailable) refreshActions()
            else showNetworkError()
        }
    }
    private fun refreshActions() {
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            actionUpdateReceiver,
            IntentFilter(Utils.getPackage(requireContext()).toString() + ".ACTIONS_DOWNLOADED")
        )
        Hover.updateActionConfigs(this, requireContext())
    }

    private fun showNetworkError() {
        pullToRefresh.isRefreshing = false
        UIHelper.flashMessage(
            requireContext(),
            if (activity != null) requireActivity().currentFocus else null,
            requireContext().getString(R.string.NO_NETWORK)
        )
    }

    var actionUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            unregisterReceiver()
            pullToRefresh.isRefreshing = false
        }
    }

    private fun unregisterReceiver() {
        try {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(actionUpdateReceiver)
        } catch (ignored: Exception) {
        }
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
        UIHelper.flashMessage(requireContext(), resources.getString(R.string.refreshed_successfully))
    }

    override fun onActionItemClick(actionId: String, titleTextView: View) {
        actionNavigationInterface.navActionDetails(actionId, titleTextView)
    }
}