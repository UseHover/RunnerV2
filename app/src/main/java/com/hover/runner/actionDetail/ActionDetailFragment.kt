package com.hover.runner.actionDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.actions.adapters.VariableRecyclerAdapter
import com.hover.runner.actions.StyledAction
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.customViews.detailsTopLayout.DetailScreenType
import com.hover.runner.customViews.detailsTopLayout.RunnerTopDetailsView
import com.hover.runner.databinding.ActionDetailsFragmentBinding
import com.hover.runner.home.SDKCallerInterface
import com.hover.runner.parser.listeners.ParserClickListener
import com.hover.runner.transaction.adapters.TransactionRecyclerAdapter
import com.hover.runner.transaction.listeners.TransactionClickListener
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.runner.utils.setSafeOnClickListener
import com.hover.sdk.actions.HoverAction
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ActionDetailFragment : BaseFragment(), ParserClickListener,
	TransactionClickListener {
	private val maxTransactionListSize = 10

	private val actionViewModel: ActionDetailViewModel by sharedViewModel()

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

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = ActionDetailsFragmentBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		initObservers()
		actionViewModel.loadAction(requireArguments().getString("action_id", ""))
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

		binding.viewAll.setOnClickListener {
			actionViewModel.action.value?.let {
				val bundle = bundleOf("action_id" to it.public_id)
				findNavController().navigate(R.id.navigation_transactions, bundle)
			}
		}

		binding.testSingleId.setSafeOnClickListener {
			actionViewModel.action.value?.let {
				val bundle = bundleOf("action_id" to it.public_id)
				findNavController().navigate(R.id.navigation_uncompletedVariableFragment, bundle)
			}
		}
	}

	private fun initObservers() {
		actionViewModel.action.observe(viewLifecycleOwner) {
			setupTopDetailsLayout(StyledAction(it))
		}
		actionViewModel.transactions.observe(viewLifecycleOwner) {
			it?.let { setTransactionsList(it) }
		}
	}

	private fun setupTopDetailsLayout(action: StyledAction) {
		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).get(action.getLayoutColor()))
		topLayout.setTitle(action.public_id, action.status)
		topLayout.setSubTitle(action.name, action.status)
		topLayout.setup(action.status, DetailScreenType.ACTION, requireActivity())
	}

	private fun setTransactionsList(transactions: List<RunnerTransaction>) {
		binding.transactionRecycler.setLayoutManagerToLinear()

		if (transactions.isEmpty()) {
			binding.recentHeader.setText(R.string.zero_transactions)
		} else {
			binding.recentHeader.setText(R.string.recent_transactions)

			if (transactions.size > maxTransactionListSize) binding.viewAll.visibility = VISIBLE
			else binding.viewAll.visibility = GONE

			binding.transactionRecycler.adapter = TransactionRecyclerAdapter(transactions, this)
		}
	}

	private fun setVariableEditRecyclerAdapter(action: HoverAction) {
		val variablesRecyclerView: RecyclerView = binding.actionVariablesRecyclerView
		variablesRecyclerView.setLayoutManagerToLinear()
		variablesRecyclerView.adapter = VariableRecyclerAdapter(action)
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

//
//	private fun setDetailTexts(action: HoverAction) {
//		operatorsText.text = action.operators
//		if (action.streamlinedSteps != null) stepsText.text =
//			action.streamlinedSteps!!.fullUSSDCodeStep
//
//		action.parsers?.let { Parser.convertTextToLinks(it, parsersText, this) }
//		transacText.text = action.transactionsNo
//		successText.text = action.successNo
//		pendingText.text = action.pendingNo
//		failureText.text = action.failedNo
//	}

	override fun onParserItemClicked(id: String) {
		findNavController().navigate(R.id.navigation_parserDetails, bundleOf("actionId" to id))
	}

	override fun onTransactionItemClicked(uuid: String) {
		findNavController().navigate(R.id.navigation_transactionDetails, bundleOf("uuid" to uuid))
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}