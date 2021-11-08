package com.hover.runner.actions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.actions.adapters.VariableRecyclerAdapter
import com.hover.runner.actions.listeners.ActionVariableEditListener
import com.hover.runner.actions.models.Action
import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.models.ActionVariablesCache
import com.hover.runner.actions.navigation.ActionNavigationInterface
import com.hover.runner.actions.viewmodel.ActionViewModel
import com.hover.runner.customViews.detailsTopLayout.DetailScreenType
import com.hover.runner.customViews.detailsTopLayout.RunnerTopDetailsView
import com.hover.runner.databinding.ActionDetailsFragmentBinding
import com.hover.runner.parser.Parser
import com.hover.runner.parser.ParserClickListener
import com.hover.runner.transactions.listeners.TransactionClickListener
import com.hover.runner.transactions.adapters.TransactionRecyclerAdapter
import com.hover.runner.transactions.viewmodel.TransactionViewModel
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class ActionDetailsFragment: Fragment(), ActionVariableEditListener, ParserClickListener,
    TransactionClickListener {
    private var timer = Timer()
    private val maxTransactionListSize = 10
    private val actionViewModel: ActionViewModel by sharedViewModel()
    private val transactionViewModel : TransactionViewModel by sharedViewModel()

    private var _binding: ActionDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private val actionNavigationInterface = activity as ActionNavigationInterface
    

    private lateinit var topLayout: RunnerTopDetailsView
    private lateinit var operatorsText: TextView
    private lateinit var stepsText: TextView
    private lateinit var parsersText: TextView
    private lateinit var transacText: TextView
    private lateinit var successText: TextView
    private lateinit var pendingText: TextView
    private lateinit var failureText: TextView
    private lateinit var testSingleActionText : TextView
    private lateinit var actionId : String

    private lateinit var recentTransactionTextView : TextView
    private lateinit var viewAllTransactionsTextView : TextView
    private lateinit var transactionRecyclerView : RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ActionDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewAllTransactionsTextView.setOnClickListener{actionNavigationInterface.navTransactionListFragment(actionId)}
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

        recentTransactionTextView = binding.recentTransaId
        viewAllTransactionsTextView = binding.viewAllId

    }

    override fun onResume() {
        super.onResume()
        loadAction()
    }

    private fun loadAction() {
        lifecycleScope.launch(Dispatchers.Main) {
            val action = actionViewModel.getAction(requireArguments().getString("action_id", ""))
            actionId = action.id!!
            setupTopDetailsLayout(action)
            actionViewModel.loadActionDetail(action.id!!)
            observeActionDetails(action)

        }
    }


    private fun setupTopDetailsLayout(action: Action) {
        UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).get(action.getStatusColor()) )
        topLayout.setup(action.statusToString(), DetailScreenType.ACTION, requireActivity())
    }

    private fun observeActionDetails(action: Action) {
        setVariableEditList(action)
        setTransactionsList(action.id!!)
    }

    private fun setTransactionsList(actionId: String) {
        transactionRecyclerView = binding.actionTransacRecyclerView
        transactionRecyclerView.layoutManager = UIHelper.setMainLinearManagers(context)

        transactionViewModel.getTransactionsByAction(actionId, maxTransactionListSize + 1).observe(viewLifecycleOwner) { transactions ->
                if(transactions !=null) {
                    if(transactions.isEmpty()) {
                        recentTransactionTextView.setText(R.string.zero_transactions)
                    }
                    else {
                        recentTransactionTextView.setText(R.string.recent_transactions)
                        if(transactions.size > maxTransactionListSize) viewAllTransactionsTextView.visibility = VISIBLE
                        else viewAllTransactionsTextView.visibility = GONE

                        transactionRecyclerView.adapter = TransactionRecyclerAdapter(transactions, this)
                    }
                }
                 else recentTransactionTextView.setText(R.string.loadingText)
            }
    }

    private fun setVariableEditList(action: Action) {
        actionViewModel.actionDetailsLiveData.observe(viewLifecycleOwner) { actionDetail ->
            if (actionDetail != null) {
                setDetailTexts(actionDetail)
                if (actionDetail.streamlinedStepsModel?.stepVariableLabel!!.isEmpty()) setVariableEditsVisibiltyGone()
                else setVariableEditRecyclerAdapter(action, actionDetail)
            }
        }
    }
    private fun setVariableEditRecyclerAdapter(action: Action, actionDetail: ActionDetails) {
        val variablesRecyclerView: RecyclerView =  binding.actionVariablesRecyclerView
        variablesRecyclerView.layoutManager = UIHelper.setMainLinearManagers(requireContext())
        variablesRecyclerView.adapter = action.id?.let {
            VariableRecyclerAdapter(
                it, actionDetail.streamlinedStepsModel, this,
                ActionVariablesCache.get(requireContext(),it).actionMap
            )
        }
    }

    private fun setVariableEditsVisibiltyGone() {
        binding.variableLabelGroup1.visibility = GONE
        binding.variableLabelGroup2.visibility = GONE
        binding.variableLabelGroup3.visibility = GONE
    }
    private fun setDetailTexts(actionDetail: ActionDetails) {
        operatorsText.text = actionDetail.operators
        if (actionDetail.streamlinedStepsModel != null) stepsText.text = actionDetail.streamlinedStepsModel!!.fullUSSDCodeStep

        actionDetail.parsers?.let { Parser.convertTextToLinks(it, parsersText, this) }
        transacText.text = actionDetail.transactionsNo
        successText.text = actionDetail.successNo
        pendingText.text = actionDetail.pendingNo
        failureText.text = actionDetail.failedNo
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun updateVariableCache(label: String, value: String) {
        timer.cancel()
        timer = Timer();
        val delay: Long = 800
        val task = object : TimerTask() {override fun run() { ActionVariablesCache.save(actionId, label, value, requireContext()) }}
        timer.schedule(task, delay)
    }

    override fun onParserItemClicked(id: String) {
        actionNavigationInterface.navParserFragment(Integer.valueOf(id))
    }

    override fun onTransactionItemClicked(uuid: String) {
        actionNavigationInterface.navTransactionDetails(uuid)
    }

}