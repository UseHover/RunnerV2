package com.hover.runner.actionDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.actions.adapters.VariableRecyclerAdapter
import com.hover.runner.actions.StyledAction
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.ActionDetailsFragmentBinding
import com.hover.runner.parser.listeners.ParserClickListener
import com.hover.runner.transaction.adapters.TransactionRecyclerAdapter
import com.hover.runner.transaction.listeners.TransactionClickListener
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.runner.utils.setSafeOnClickListener
import com.hover.sdk.actions.HoverAction
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ActionDetailFragment : BaseFragment(), ParserClickListener, TransactionClickListener {

	private val actionViewModel: ActionDetailViewModel by sharedViewModel()
	private var _binding: ActionDetailsFragmentBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = ActionDetailsFragmentBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initListeners()
		initObservers()
		actionViewModel.loadAction(requireArguments().getString("action_id", ""))
	}

	private fun initListeners() {
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
		actionViewModel.action.observe(viewLifecycleOwner) { it?.let { fillDetails(it) } }

		actionViewModel.transactions.observe(viewLifecycleOwner) {
			fillTransactionDetails(it)
			binding.header.setStatus(if (!it.isNullOrEmpty()) it[0].status else null, requireActivity())
		}

		actionViewModel.successCount.observe(viewLifecycleOwner) { it?.let { binding.successCount.text = it.toString() } }
		actionViewModel.failedCount.observe(viewLifecycleOwner) { it?.let { binding.failedCount.text = it.toString() } }
		actionViewModel.pendingCount.observe(viewLifecycleOwner) { it?.let { binding.pendingCount.text = it.toString() } }
	}

	private fun fillDetails(action: HoverAction) {
		binding.header.setAction(StyledAction(action))
		binding.operators.text = action.network_name
//		binding.longcode.text = action.generateLongcode();
//		binding.parsersContent = action.pa

		//		if (action.streamlinedSteps != null) stepsText.text =
		//			action.streamlinedSteps!!.fullUSSDCodeStep

		//		action.parsers?.let { Parser.convertTextToLinks(it, parsersText, this) }
	}

	private fun fillTransactionDetails(transactions: List<RunnerTransaction>) {
		if (!transactions.isNullOrEmpty()) setTransactionsList(transactions)
		else binding.recentHeader.setText(R.string.zero_transactions)

		binding.transactionCount.text = transactions.size.toString()

		//		successText.text = action.successNo
		//		pendingText.text = action.pendingNo
		//		failureText.text = action.failedNo
	}

	private fun setTransactionsList(transactions: List<RunnerTransaction>) {
		binding.recentHeader.setText(R.string.recent_transactions)
		binding.transactionRecycler.setLayoutManagerToLinear()
		binding.transactionRecycler.adapter = TransactionRecyclerAdapter(transactions, this)
		binding.viewAll.visibility = if (transactions.size > ActionDetailViewModel.T_LIMIT - 1) VISIBLE else GONE
	}

	private fun setVariableEditRecyclerAdapter(action: HoverAction) {
		val variablesRecyclerView: RecyclerView = binding.actionVariablesRecyclerView
		variablesRecyclerView.setLayoutManagerToLinear()
		variablesRecyclerView.adapter = VariableRecyclerAdapter(action)
	}

	override fun onParserItemClicked(id: String) {
		findNavController().navigate(R.id.navigation_parserDetails, bundleOf("action_id" to id))
	}

	override fun onTransactionItemClicked(uuid: String) {
		findNavController().navigate(R.id.navigation_transactionDetails, bundleOf("uuid" to uuid))
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}