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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.hover.runner.ApplicationInstance
import com.hover.runner.R
import com.hover.runner.actions.viewmodel.ActionViewModel
import com.hover.runner.databinding.FragmentActionsBinding
import com.hover.runner.utils.NetworkUtil
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.Utils
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.Exception
import java.util.ArrayList

class ActionsFragment  : Fragment(), Hover.DownloadListener {

    private var _binding: FragmentActionsBinding? = null
    private val actionViewModel: ActionViewModel by sharedViewModel()

    private lateinit var progressBar: ProgressBar
    private lateinit var actionsRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout

    private lateinit var emptyInfoLayout: LinearLayout
    private lateinit var emptyStateView: RelativeLayout
    private lateinit var emptyTitle: TextView
    private lateinit var emptySubtitle:TextView


    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentActionsBinding.inflate(inflater, container, false)
        initViews()
        pullToRefresh.isRefreshing = false
        setupRecyclerView()

        return binding.root
    }

    private fun initViews() {
        progressBar = binding.recyclerViewState.progressState1
        emptyInfoLayout = binding.recyclerViewState.emptyInfoLayout
        emptyStateView = binding.recyclerViewState.layoutForEmptyStateId
        emptyTitle = binding.recyclerViewState.emptyTitleText
        emptySubtitle = binding.recyclerViewState.emptySubtitleText

        pullToRefresh = binding.pullToRefresh
        actionsRecyclerView = binding.recyclerView.recyclerViewId

    }

    private fun setupRecyclerView() {
        actionsRecyclerView.layoutManager = UIHelper.setMainLinearManagers(context)
        actionsRecyclerView.setHasFixedSize(true)
    }

    private fun setupPullToRefresh() {
        pullToRefresh.setOnRefreshListener {
            if (NetworkUtil(requireContext()).isNetworkAvailable) {
                LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                    actionUpdateReceiver,
                    IntentFilter(Utils.getPackage(requireContext()).toString() + ".ACTIONS_DOWNLOADED")
                )
                Hover.updateActionConfigs(this, requireContext())
            } else {
                pullToRefresh.isRefreshing = false
                UIHelper.flashMessage(
                    requireContext(),
                    if (activity != null) requireActivity().currentFocus else null,
                    requireContext().getString(R.string.NO_NETWORK)
                )
            }
        }
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
        TODO("Not yet implemented")
    }

    override fun onSuccess(actionList: ArrayList<HoverAction>?) {
        TODO("Not yet implemented")
    }
}