package com.hover.runner.actionDetails

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.databinding.FragmentActionDetailsBinding
import com.hover.runner.main.BaseFragment
import com.hover.runner.transactions.TransactionsRecyclerAdapter
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.parsers.HoverParser
import com.hover.sdk.transactions.Transaction
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class ActionDetailFragment : BaseFragment(), TransactionsRecyclerAdapter.TransactionClickListener {

	private val actionViewModel: ActionDetailViewModel by sharedViewModel()
	private var _binding: FragmentActionDetailsBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

		_binding = FragmentActionDetailsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val actionId = requireArguments().getString("action_id", "")
		initListeners()
		initObservers()
		actionViewModel.loadAction(actionId)
	}

	private fun initListeners() {
		binding.viewAll.setOnClickListener {
			actionViewModel.action.value?.let {
				val bundle = bundleOf("action_id" to it.public_id)
				findNavController().navigate(R.id.navigation_transactions, bundle)
			}
		}

		binding.startTest.setOnClickListener {
			attemptStartTest()
		}
	}

	private fun attemptStartTest() {
		if (actionViewModel.action.value == null || !allVarsFilled(actionViewModel.action.value!!))
			UIHelper.flashMessage(requireContext(), getString(R.string.summary_incomplete))
		else
			startTest()
	}

	private fun startTest() {
		val bundle = bundleOf("action_id" to actionViewModel.action.value!!.public_id)
		findNavController().navigate(R.id.navigation_run_variables, bundle)
	}

	private fun allVarsFilled(action: HoverAction): Boolean {
		for (key in action.requiredParams) {
			if (SharedPrefUtils.getVarValue(action.public_id, key, requireContext()).isEmpty())
				return false
		}
		return true
	}

	private fun initObservers() {
		actionViewModel.action.observe(viewLifecycleOwner) { it?.let { fillDetails(it) } }

		actionViewModel.transactions.observe(viewLifecycleOwner) {
			Timber.i("how many transactions? : ${it.size}")
			fillTransactionDetails(it)
			binding.header.setStatus(if (!it.isNullOrEmpty()) it[0].status else null, requireActivity())
		}

		actionViewModel.parsers.observe(viewLifecycleOwner) {
			it?.let { fillParserDetails(it, binding.parsers) }
		}
	}

	private fun fillDetails(action: HoverAction) {
		binding.header.setAction(action)
		binding.operators.text = action.network_name
		binding.longcode.text = action.longcode(requireContext())
		showVariables(action)
	}

	private fun showVariables(action: HoverAction) {
		binding.actionVariables.setLayoutManagerToLinear()
		binding.actionVariables.adapter = VariableRecyclerAdapter(action)
		binding.variableSection.visibility = if (action.requiredParams.size > 0) VISIBLE else GONE
	}

	private fun fillParserDetails(parsers: List<HoverParser>, tv: TextView) {
		val ss = SpannableString(parsers.joinToString { it.category })
		var start = 0
		var end: Int
		for (item in parsers) {
			end = start + item.category.length
			if (start < end) {
				ss.setSpan(UnderlineSpan(), start, end, 0)
				ss.setSpan(MyClickableSpan(item, findNavController()), start, end, 0)
				ss.setSpan(ForegroundColorSpan(Color.WHITE), start, end, 0)
			}
			start += item.category.toString().length + 2
		}
		tv.movementMethod = LinkMovementMethod.getInstance()
		Timber.e("printing: $ss")
		tv.setText(ss, TextView.BufferType.SPANNABLE)
	}

	private fun fillTransactionDetails(transactions: List<Transaction>) {
		if (!transactions.isNullOrEmpty()) setTransactionsList(transactions)
		else {
			binding.recentHeader.setText(R.string.zero_transactions)
			binding.transactionRecycler.adapter = null
		}
		setStatusCounts(transactions)
	}

	private fun setStatusCounts(transactions: List<Transaction>) {
		binding.transactionSummary.transactionCount.text = transactions.size.toString()
		binding.transactionSummary.successCount.text = transactions.count { it.status == Transaction.SUCCEEDED}.toString()
		binding.transactionSummary.pendingCount.text = transactions.count { it.status == Transaction.PENDING}.toString()
		binding.transactionSummary.failedCount.text = transactions.count { it.status == Transaction.FAILED}.toString()
	}

	private fun setTransactionsList(transactions: List<Transaction>) {
		binding.recentHeader.setText(R.string.recent_transactions)
		binding.transactionRecycler.setLayoutManagerToLinear()
		binding.transactionRecycler.adapter = TransactionsRecyclerAdapter(transactions, this)
		binding.viewAll.visibility = if (transactions.size > ActionDetailViewModel.T_LIMIT - 1) VISIBLE else GONE
	}

	override fun onItemClick(uuid: String) {
		findNavController().navigate(R.id.navigation_transactionDetails, bundleOf("uuid" to uuid))
	}

	override fun onPause() {
		super.onPause()
		UIHelper.changeStatusBarColor(requireActivity(), getColor(requireContext(), R.color.runnerPrimary))
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private class MyClickableSpan(private val parser: HoverParser, private val navController: NavController) : ClickableSpan() {
		override fun onClick(widget: View) {
			navController.navigate(R.id.navigation_parserDetails, bundleOf("parser_id" to parser.id))
		}
	}
}