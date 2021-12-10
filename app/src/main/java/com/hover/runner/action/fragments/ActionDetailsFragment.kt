package com.hover.runner.action.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.action.adapters.VariableRecyclerAdapter
import com.hover.runner.action.listeners.ActionVariableEditListener
import com.hover.runner.action.models.Action
import com.hover.runner.action.models.ActionDetails
import com.hover.runner.action.models.ActionVariablesCache
import com.hover.runner.action.navigation.ActionNavigationInterface
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.customViews.detailsTopLayout.DetailScreenType
import com.hover.runner.customViews.detailsTopLayout.RunnerTopDetailsView
import com.hover.runner.databinding.ActionDetailsFragmentBinding
import com.hover.runner.home.SDKCallerInterface
import com.hover.runner.parser.listeners.ParserClickListener
import com.hover.runner.parser.model.Parser
import com.hover.runner.transaction.adapters.TransactionRecyclerAdapter
import com.hover.runner.transaction.listeners.TransactionClickListener
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.runner.utils.setSafeOnClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class ActionDetailsFragment : BaseFragment(), ActionVariableEditListener, ParserClickListener,
	TransactionClickListener {
	private var timer = Timer()

	private val maxTransactionListSize = 10

	private val actionViewModel: ActionViewModel by sharedViewModel()
	private val transactionViewModel: TransactionViewModel by sharedViewModel()

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
	private lateinit var testSingleActionText: TextView
	private lateinit var actionId: String

	private lateinit var recentTransactionTextView: TextView
	private lateinit var viewAllTransactionsTextView: TextView
	private lateinit var transactionRecyclerView: RecyclerView

	private lateinit var sdkCallerInterface: SDKCallerInterface
	private lateinit var actionNavigationInterface: ActionNavigationInterface


	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View {
		_binding = ActionDetailsFragmentBinding.inflate(inflater, container, false)
		initNavigationInterface()
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		viewAllTransactionsTextView.setOnClickListener {
			actionNavigationInterface.navTransactionListFragment(actionId)
		}
	}

	private fun initNavigationInterface() {
		sdkCallerInterface = activity as SDKCallerInterface
		actionNavigationInterface = activity as ActionNavigationInterface
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
			actionId = action.id
			setupTopDetailsLayout(action)
			observeActionDetails(action)
			setupTestSingleAction(actionId)
		}
	}

	private fun setupTestSingleAction(actionId: String) {
		testSingleActionText.setSafeOnClickListener {
			sdkCallerInterface.runAction(actionId)
		}
	}

	private fun setupTopDetailsLayout(action: Action) {
		UIHelper.changeStatusBarColor(requireActivity(),
		                              RunnerColor(requireContext()).get(action.getLayoutColor()))
		topLayout.setTitle(action.id, action.status)
		topLayout.setSubTitle(action.title, action.status)
		topLayout.setup(action.status, DetailScreenType.ACTION, requireActivity())
	}

	private fun observeActionDetails(action: Action) {
		actionViewModel.getActionDetail(action.id)
		setVariableEditList(action)
		setTransactionsList(action.id)
	}

	private fun setTransactionsList(actionId: String) {
		transactionRecyclerView = binding.actionTransacRecyclerView
		transactionRecyclerView.setLayoutManagerToLinear()

		transactionViewModel.observeTransactionsByAction(actionId, (maxTransactionListSize + 1))
			.observe(viewLifecycleOwner) { transactions ->
				if (transactions != null) {
					if (transactions.isEmpty()) {
						recentTransactionTextView.setText(R.string.zero_transactions)
					}
					else {
						recentTransactionTextView.setText(R.string.recent_transactions)
						if (transactions.size > maxTransactionListSize) viewAllTransactionsTextView.visibility =
							VISIBLE
						else viewAllTransactionsTextView.visibility = GONE

						transactionRecyclerView.adapter =
							TransactionRecyclerAdapter(transactions, this)
					}
				}
				else recentTransactionTextView.setText(R.string.loadingText)
			}
	}

	private fun setVariableEditList(action: Action) {
		actionViewModel.actionDetailsLiveData.observe(viewLifecycleOwner) { actionDetail ->
			if (actionDetail != null) {
				setDetailTexts(actionDetail)
				if (actionDetail.streamlinedSteps?.stepVariableLabel!!.isEmpty()) setVariableEditsVisibilityGone()
				else {
					makeVariableEditsVisible()
					setVariableEditRecyclerAdapter(action, actionDetail)
				}
			}
		}
	}

	private fun setVariableEditRecyclerAdapter(action: Action, actionDetail: ActionDetails) {
		val variablesRecyclerView: RecyclerView = binding.actionVariablesRecyclerView
		variablesRecyclerView.setLayoutManagerToLinear()
		variablesRecyclerView.adapter = action.id.let {
			VariableRecyclerAdapter(it,
			                        actionDetail.streamlinedSteps,
			                        this,
			                        ActionVariablesCache.get(requireContext(), it).actionMap)
		}
	}

	private fun setVariableEditsVisibilityGone() {
		binding.variableLabelGroup1.visibility = GONE
		binding.variableLabelGroup2.visibility = GONE
		binding.variableLabelGroup3.visibility = GONE
	}

	private fun makeVariableEditsVisible() {
		binding.variableLabelGroup1.visibility = VISIBLE
		binding.variableLabelGroup2.visibility = VISIBLE
		binding.variableLabelGroup3.visibility = VISIBLE
	}


	private fun setDetailTexts(actionDetail: ActionDetails) {
		operatorsText.text = actionDetail.operators
		if (actionDetail.streamlinedSteps != null) stepsText.text =
			actionDetail.streamlinedSteps!!.fullUSSDCodeStep

		actionDetail.parsers?.let { Parser.convertTextToLinks(it, parsersText, this) }
		transacText.text = actionDetail.transactionsNo
		successText.text = actionDetail.successNo
		pendingText.text = actionDetail.pendingNo
		failureText.text = actionDetail.failedNo
	}

	override fun updateVariableCache(label: String, value: String) {
		timer.cancel()
		timer = Timer()
		val task = object : TimerTask() {
			override fun run() {
				ActionVariablesCache.save(actionId, label, value, requireContext())
			}
		}
		timer.schedule(task, ActionVariablesCache.THROTTLE)
	}

	override fun onParserItemClicked(id: String) {
		actionNavigationInterface.navParserDetailsFragment(actionId, Integer.valueOf(id))
	}

	override fun onTransactionItemClicked(uuid: String) {
		actionNavigationInterface.navTransactionDetails(uuid)
	}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}