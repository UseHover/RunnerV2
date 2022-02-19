package com.hover.runner.transactionDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.databinding.FragmentTransactionDetailsBinding
import com.hover.runner.main.DetailsHeaderView
import com.hover.runner.parser.ParserClickListener
import com.hover.runner.utils.DateUtils
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.transactions.Transaction
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class TransactionDetailsFragment : Fragment(), ParserClickListener {
	private var _binding: FragmentTransactionDetailsBinding? = null
	private val binding get() = _binding!!

	private val viewModel: TransactionDetailsViewModel by sharedViewModel()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onResume() {
		super.onResume()
		loadTransaction()
	}

	private fun loadTransaction() {
		viewModel.transaction.observe(viewLifecycleOwner) {
			it?.let { fillDetails(it) }
			viewModel.action.value?.let { a -> createMessagesAdapter(it, a) }
		}

		viewModel.action.observe(viewLifecycleOwner) {
			Timber.e("Loaded action: %s", it?.public_id?: "null")
			viewModel.transaction.value?.let { t -> createMessagesAdapter(t, it) } }
		viewModel.load(requireArguments().getString("uuid", ""))
	}

	private fun fillDetails(transaction: Transaction) {
		UIHelper.changeStatusBarColor(requireActivity(), UIHelper.getStatusColor(transaction.status, requireContext()))
		binding.transactionDetailsTopLayoutId.setTransaction(transaction, requireActivity())
		binding.valueStatus.text = transaction.status
		binding.valueCategory.text = transaction.category
		binding.valueActionId.text = transaction.actionId
		binding.valueTime.text = DateUtils.humanFriendlyDateTime(transaction.reqTimestamp)
		binding.valueTransactionId.text = transaction.uuid
		binding.valueResult.text = transaction.userMessage
	}

	private fun createMessagesAdapter(t: Transaction, a: HoverAction) {
		binding.valueAction.text = a.name
		val messages = TransactionMessages.generateConvo(t, a)
		binding.transacMessagesRecyclerView.setLayoutManagerToLinear()
		val messagesInfoAdapter = TransactionMessagesRecyclerAdapter(messages)
		binding.transacMessagesRecyclerView.adapter = messagesInfoAdapter
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onParserItemClicked(id: Int) {
		findNavController().navigate(R.id.navigation_parserDetails, bundleOf("parser_id" to id))
	}
}