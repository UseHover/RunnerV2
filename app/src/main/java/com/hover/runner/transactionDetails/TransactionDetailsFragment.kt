package com.hover.runner.transactionDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.databinding.FragmentTransactionDetailsBinding
import com.hover.runner.parser.ParserClickListener
import com.hover.runner.utils.DateUtils
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

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		loadTransaction()
	}
	private fun loadTransaction() {
		viewModel.load(requireArguments().getString("uuid", ""))

		viewModel.transaction.observe(viewLifecycleOwner) {
			it?.let { fillDetails(it) }
			viewModel.action.value?.let { a -> createMessagesAdapter(it, a) }
		}

		viewModel.action.observe(viewLifecycleOwner) {
			Timber.e("Loaded action: %s", it?.public_id?: "null")
			viewModel.transaction.value?.let { t -> createMessagesAdapter(t, it) } }
	}

	private fun fillDetails(transaction: Transaction) {
		binding.transactionDetailsTopLayoutId.setTransaction(transaction, requireActivity())
		binding.valueStatus.text = transaction.status
		binding.valueCategory.text = transaction.category
		binding.valueActionId.text = transaction.actionId
		binding.valueTime.text = DateUtils.humanFriendlyDateTime(transaction.reqTimestamp)
		binding.valueTransactionId.text = transaction.uuid
		binding.valueEnvironment.setText(getReadableEnv(transaction.env))
		binding.valueResult.text = transaction.userMessage
	}

	private fun getReadableEnv(env: Int) : Int {
		return when(env) {
			0 -> R.string.normal_mode
			1 -> R.string.debug_mode
			else -> R.string.test_mode
		}
	}

	private fun createMessagesAdapter(t: Transaction, a: HoverAction) {
		binding.valueAction.text = a.name
		val messages = TransactionMessages.generateConvo(t, a)
		binding.transacMessagesRecyclerView.setLayoutManagerToLinear()
		val messagesInfoAdapter = TransactionMessagesRecyclerAdapter(messages)
		binding.transacMessagesRecyclerView.adapter = messagesInfoAdapter
	}

	override fun onPause() {
		super.onPause()
		UIHelper.changeStatusBarColor(requireActivity(), ContextCompat.getColor(requireContext(), R.color.runnerPrimary))
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onParserItemClicked(id: Int) {
		findNavController().navigate(R.id.navigation_parserDetails, bundleOf("parser_id" to id))
	}
}