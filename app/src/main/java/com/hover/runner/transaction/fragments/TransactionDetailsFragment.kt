package com.hover.runner.transaction.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.action.listeners.ActionClickListener
import com.hover.runner.action.navigation.ActionNavigationInterface
import com.hover.runner.customViews.detailsTopLayout.DetailScreenType
import com.hover.runner.customViews.detailsTopLayout.RunnerTopDetailsView
import com.hover.runner.databinding.TransactionDetailsFragmentBinding
import com.hover.runner.parser.listeners.ParserClickListener
import com.hover.runner.parser.navigation.ParserNavigationInterface
import com.hover.runner.transaction.adapters.TransactionDetailsRecyclerAdapter
import com.hover.runner.transaction.adapters.TransactionMessagesRecyclerAdapter
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.model.TransactionDetailsInfo
import com.hover.runner.transaction.model.TransactionDetailsMessages
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TransactionDetailsFragment : Fragment(), ActionClickListener, ParserClickListener {
    private var _binding: TransactionDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by sharedViewModel()

    private lateinit var topLayout: RunnerTopDetailsView

    private lateinit var actionNavigationInterface: ActionNavigationInterface
    private lateinit var parserNavigationInterface: ParserNavigationInterface

    private lateinit var aboutInfoRecyclerView: RecyclerView
    private lateinit var deviceInfoRecyclerView: RecyclerView
    private lateinit var debugInfoRecyclerView: RecyclerView
    private lateinit var messagesInfoRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TransactionDetailsFragmentBinding.inflate(inflater, container, false)
        initInterface()
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

    private fun initInterface() {
        actionNavigationInterface = activity as ActionNavigationInterface
        parserNavigationInterface = activity as ParserNavigationInterface
    }

    private fun initViews() {
        topLayout = binding.transactionDetailsTopLayoutId
        aboutInfoRecyclerView = binding.transacAboutInfoRecyclerView
        deviceInfoRecyclerView = binding.transacDebugInfoRecyclerView
        debugInfoRecyclerView = binding.transacDebugInfoRecyclerView
        messagesInfoRecyclerView = binding.transacMessagesRecyclerView
    }

    private fun initRecyclerViewsLayoutManager() {
        aboutInfoRecyclerView.layoutManager = UIHelper.setMainLinearManagers(requireContext())
        deviceInfoRecyclerView.layoutManager = UIHelper.setMainLinearManagers(requireContext())
        debugInfoRecyclerView.layoutManager = UIHelper.setMainLinearManagers(requireContext())
        messagesInfoRecyclerView.layoutManager = UIHelper.setMainLinearManagers(requireContext())
    }

    private fun loadTransaction() {
        transactionViewModel.observeTransaction(arguments?.getString("uuid")!!)
            .observe(viewLifecycleOwner) {
                setupTopDetailsLayout(it)
                observeAboutInfo(it)
                observeDeviceInfo(it)
                observeDebugInfo(it)
                observeTransactionMessages(it)
            }
    }

    private fun setupTopDetailsLayout(transaction: RunnerTransaction) {
        UIHelper.changeStatusBarColor(
            requireActivity(),
            RunnerColor(requireContext()).get(transaction.getStatusColor())
        )
        topLayout.setTitle(transaction.getDate()!!, transaction.status)
        topLayout.setSubTitle(transaction.uuid, transaction.status)
        topLayout.setup(transaction.status, DetailScreenType.TRANSACTION, requireActivity())
    }

    private fun observeAboutInfo(transaction: RunnerTransaction) {
        transactionViewModel.observeAboutInfo(transaction).observe(viewLifecycleOwner) { info ->
            info?.let { setAboutInfoAdapter(it) }
        }
    }

    private fun observeDeviceInfo(transaction: RunnerTransaction) {
        transactionViewModel.observeDeviceInfo(transaction).observe(viewLifecycleOwner) { info ->
            info?.let { setDeviceInfoAdapter(it) }
        }
    }

    private fun observeDebugInfo(transaction: RunnerTransaction) {
        transactionViewModel.observeDebugInfo(transaction).observe(viewLifecycleOwner) { info ->
            info?.let { setDebugInfoAdapter(it) }
        }
    }

    private fun observeTransactionMessages(transaction: RunnerTransaction) {
        transactionViewModel.observeTransactionMessages(transaction)
            .observe(viewLifecycleOwner) { messages ->
                messages?.let { setMessagesAdapter(it) }
            }
    }

    private fun setAboutInfoAdapter(transactionDetailsInfo: List<TransactionDetailsInfo>) {
        if (aboutInfoRecyclerView.adapter == null) {
            aboutInfoRecyclerView.adapter =
                TransactionDetailsRecyclerAdapter(transactionDetailsInfo, this, this, true)
        }
    }

    private fun setDeviceInfoAdapter(transactionDetailsInfo: List<TransactionDetailsInfo>) {
        if (deviceInfoRecyclerView.adapter == null) {
            deviceInfoRecyclerView.adapter =
                TransactionDetailsRecyclerAdapter(transactionDetailsInfo, this, this)
        }
    }

    private fun setDebugInfoAdapter(transactionDetailsInfo: List<TransactionDetailsInfo>) {
        if (debugInfoRecyclerView.adapter == null) {
            debugInfoRecyclerView.adapter =
                TransactionDetailsRecyclerAdapter(transactionDetailsInfo, this, this)
        }
    }

    private fun setMessagesAdapter(transactionDetailsMessages: List<TransactionDetailsMessages>) {
        if (messagesInfoRecyclerView.adapter == null) {
            messagesInfoRecyclerView.adapter =
                TransactionMessagesRecyclerAdapter(transactionDetailsMessages)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActionItemClick(actionId: String, titleTextView: View) {
        actionNavigationInterface.navActionDetails(actionId, titleTextView)
    }

    override fun onParserItemClicked(id: String) {
        parserNavigationInterface.navParserDetailsFragment(id.toInt())
    }
}