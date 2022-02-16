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
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.home.BaseFragment
import com.hover.runner.databinding.ActionDetailsFragmentBinding
import com.hover.runner.transaction.adapters.TransactionRecyclerAdapter
import com.hover.runner.transaction.listeners.TransactionClickListener
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.runner.utils.setSafeOnClickListener
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.parsers.HoverParser
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class ActionDetailFragment : BaseFragment(), TransactionClickListener {

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
				findNavController().navigate(R.id.navigation_run_summary, bundle)
			}
		}
	}

	private fun initObservers() {
		actionViewModel.action.observe(viewLifecycleOwner) { it?.let { fillDetails(it) } }

		actionViewModel.transactions.observe(viewLifecycleOwner) {
			fillTransactionDetails(it)
			binding.header.setStatus(if (!it.isNullOrEmpty()) it[0].status else null, requireActivity())
		}

		actionViewModel.parsers.observe(viewLifecycleOwner) {
			it?.let { fillParserDetails(it, binding.parsers) }
		}

		actionViewModel.successCount.observe(viewLifecycleOwner) { it?.let { binding.successCount.text = it.toString() } }
		actionViewModel.failedCount.observe(viewLifecycleOwner) { it?.let { binding.failedCount.text = it.toString() } }
		actionViewModel.pendingCount.observe(viewLifecycleOwner) { it?.let { binding.pendingCount.text = it.toString() } }
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

	private fun fillTransactionDetails(transactions: List<RunnerTransaction>) {
		if (!transactions.isNullOrEmpty()) setTransactionsList(transactions)
		else binding.recentHeader.setText(R.string.zero_transactions)
		binding.transactionCount.text = transactions.size.toString()
	}

	private fun setTransactionsList(transactions: List<RunnerTransaction>) {
		binding.recentHeader.setText(R.string.recent_transactions)
		binding.transactionRecycler.setLayoutManagerToLinear()
		binding.transactionRecycler.adapter = TransactionRecyclerAdapter(transactions, this)
		binding.viewAll.visibility = if (transactions.size > ActionDetailViewModel.T_LIMIT - 1) VISIBLE else GONE
	}

	override fun onTransactionItemClicked(uuid: String) {
		findNavController().navigate(R.id.navigation_transactionDetails, bundleOf("uuid" to uuid))
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