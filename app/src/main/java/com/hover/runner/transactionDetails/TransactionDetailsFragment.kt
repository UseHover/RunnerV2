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
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.sdk.transactions.Transaction
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TransactionDetailsFragment : Fragment(), ParserClickListener {
	private var _binding: FragmentTransactionDetailsBinding? = null
	private val binding get() = _binding!!

	private val viewModel: TransactionDetailsViewModel by sharedViewModel()

	private lateinit var topLayoutHeader: DetailsHeaderView

	private lateinit var aboutInfoRecyclerView: RecyclerView
	private lateinit var deviceInfoRecyclerView: RecyclerView
	private lateinit var debugInfoRecyclerView: RecyclerView
	private lateinit var messagesInfoRecyclerView: RecyclerView

	private lateinit var messagesInfoAdapter: TransactionMessagesRecyclerAdapter

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		initRecyclerViewsLayoutManager()
	}

	override fun onResume() {
		super.onResume()
		loadTransaction()
	}

	private fun initViews() {
		aboutInfoRecyclerView = binding.transacAboutInfoRecyclerView
		deviceInfoRecyclerView = binding.transacDevicesRecyclerView
		debugInfoRecyclerView = binding.transacDebugInfoRecyclerView
		messagesInfoRecyclerView = binding.transacMessagesRecyclerView
	}

	private fun initRecyclerViewsLayoutManager() {
		aboutInfoRecyclerView.setLayoutManagerToLinear()
		deviceInfoRecyclerView.setLayoutManagerToLinear()
		debugInfoRecyclerView.setLayoutManagerToLinear()
		messagesInfoRecyclerView.setLayoutManagerToLinear()
	}

	private fun loadTransaction() {
		viewModel.transaction.observe(viewLifecycleOwner) {
			it?.let {
				setupTopDetailsLayout(it)
//				observeTransactionMessages(it)
			}
		}
	}

	private fun setupTopDetailsLayout(transaction: Transaction) {
//		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).get(transaction.getStatusColor()))
		topLayoutHeader.setTransaction(transaction, requireActivity())
	}

//	private fun observeTransactionMessages(transaction: Transaction) {
//		transactionViewModel.loadTransactionMessages(transaction)
//		transactionViewModel.messagesInfoLiveData.observe(viewLifecycleOwner) { messages ->
//			messages?.let { setMessagesAdapter(it) }
//		}
//	}

//	private fun setMessagesAdapter(transactionDetailsMessages: List<TransactionDetailsMessages>) {
//		messagesInfoAdapter = TransactionMessagesRecyclerAdapter(transactionDetailsMessages)
//		messagesInfoRecyclerView.adapter = messagesInfoAdapter
//	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onParserItemClicked(id: Int) {
		findNavController().navigate(R.id.navigation_parserDetails, bundleOf("parser_id" to id))
	}
}