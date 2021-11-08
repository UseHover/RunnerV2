package com.hover.runner.actions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.actions.adapters.VariableRecyclerAdapter
import com.hover.runner.actions.listeners.ActionVariableEditListener
import com.hover.runner.actions.models.Action
import com.hover.runner.actions.models.ActionVariablesCache
import com.hover.runner.actions.viewmodel.ActionViewModel
import com.hover.runner.customViews.detailsTopLayout.DetailScreenType
import com.hover.runner.customViews.detailsTopLayout.RunnerTopDetailsView
import com.hover.runner.databinding.ActionDetailsFragmentBinding
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class ActionDetailsFragment: Fragment(), ActionVariableEditListener {
    private var timer = Timer()
    private val actionViewModel: ActionViewModel by sharedViewModel()
    private var _binding: ActionDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    

    private lateinit var topLayout: RunnerTopDetailsView
    private lateinit var operatorsText: TextView
    private lateinit var stepsText: TextView
    private lateinit var parsersText: TextView
    private lateinit var transacText: TextView
    private lateinit var successText: TextView
    private lateinit var pendingText: TextView
    private lateinit var failureText: TextView
    private lateinit var testSingleActionText : TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ActionDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }
    private fun initViews() {
        topLayout = binding.detailsTopLayout

        operatorsText = binding.operatorsContent
        stepsText = binding.stepsContent
        parsersText = binding.parsersContent
        transacText = binding.transactionNoContent
        successText = binding.successCountContent
        pendingText = binding.pendingCountContent
        failureText = binding.failedCountContent
        testSingleActionText = binding.testSingleId

    }

    override fun onResume() {
        super.onResume()
        loadAction()
    }

    private fun loadAction() {
        lifecycleScope.launch(Dispatchers.Main) {
            val action = actionViewModel.getAction(requireArguments().getString("action_id", ""))
            setupTopDetailsLayout(action)
            actionViewModel.loadActionDetail(action.id!!)
            observeActionDetails()

        }
    }


    private fun setupTopDetailsLayout(action: Action) {
        UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).get(action.getStatusColor()) )
        topLayout.setup(action.statusToString(), DetailScreenType.ACTION, requireActivity())
    }

    private fun observeActionDetails() {
        setVariableEditList()
    }

    private fun setVariableEditList(action: Action) {
        val variablesRecyclerView: RecyclerView =  binding.actionVariablesRecyclerView

        actionViewModel.actionDetailsLiveData.observe(viewLifecycleOwner) { actionDetail ->
            if (actionDetail != null) {
                operatorsText.text = actionDetail.operators
                if (actionDetail.streamlinedStepsModel != null) stepsText.text = actionDetail.streamlinedStepsModel!!.fullUSSDCodeStep

                UIHelper.makeEachTextLinks(actionDetail.parsers, parsersText, this)
                transacText.text = actionDetail.transactionsNo
                successText.text = actionDetail.successNo
                pendingText.text = actionDetail.pendingNo
                failureText.text = actionDetail.failedNo

                if (actionDetail.streamlinedStepsModel?.stepVariableLabel!!.isEmpty()) {
                    binding.variableLabelGroup1.visibility = View.GONE
                    binding.variableLabelGroup2.visibility = View.GONE
                    binding.variableLabelGroup3.visibility = View.GONE
                } else {
                    variablesRecyclerView.layoutManager = UIHelper.setMainLinearManagers(requireContext())
                    variablesRecyclerView.adapter = action.id?.let {
                        VariableRecyclerAdapter(
                            it, actionDetail.streamlinedStepsModel, this,
                            ActionVariablesCache.get(requireContext(),it).actionMap
                        )
                    }
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDataUpdated(label: String, value: String) {
        TODO("Not yet implemented")
    }

}