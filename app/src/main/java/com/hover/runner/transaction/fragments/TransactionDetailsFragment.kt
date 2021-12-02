package com.hover.runner.transaction.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.action.listeners.ActionClickListener
import com.hover.runner.customViews.detailsTopLayout.DetailScreenType
import com.hover.runner.customViews.detailsTopLayout.RunnerTopDetailsView
import com.hover.runner.databinding.TransactionDetailsFragmentBinding
import com.hover.runner.parser.listeners.ParserClickListener
import com.hover.runner.transaction.adapters.TransactionDetailsRecyclerAdapter
import com.hover.runner.transaction.adapters.TransactionMessagesRecyclerAdapter
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.model.TransactionDetailsInfo
import com.hover.runner.transaction.model.TransactionDetailsMessages
import com.hover.runner.transaction.navigation.TransactionNavigationInterface
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class TransactionDetailsFragment : Fragment(), ActionClickListener, ParserClickListener {
    private var _binding: TransactionDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by sharedViewModel()

    private lateinit var topLayout: RunnerTopDetailsView

    private lateinit var transactionNavigationInterface: TransactionNavigationInterface

    private lateinit var aboutInfoRecyclerView: RecyclerView
    private lateinit var deviceInfoRecyclerView: RecyclerView
    private lateinit var debugInfoRecyclerView: RecyclerView
    private lateinit var messagesInfoRecyclerView: RecyclerView

    private lateinit var aboutInfoAdapter : TransactionDetailsRecyclerAdapter
    private lateinit var deviceInfoAdapter : TransactionDetailsRecyclerAdapter
    private lateinit var debugInfoAdapter : TransactionDetailsRecyclerAdapter
    private lateinit var messagesInfoAdapter:  TransactionMessagesRecyclerAdapter

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
        transactionNavigationInterface = activity as TransactionNavigationInterface
    }

    private fun initViews() {
        topLayout = binding.transactionDetailsTopLayoutId
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
        Timber.i("requested to observing about info")
        transactionViewModel.loadAboutInfo(transaction)
        transactionViewModel.aboutInfoMutableLiveData.observe(viewLifecycleOwner) { info ->
            info?.let {
                Timber.i("observing about info")
                setAboutInfoAdapter(it) }
        }
    }

    private fun observeDeviceInfo(transaction: RunnerTransaction) {
        transactionViewModel.loadDeviceInfo(transaction)
        transactionViewModel.deviceInfoMutableLiveData.observe(viewLifecycleOwner) { info ->
            info?.let { setDeviceInfoAdapter(it) }
        }
    }

    private fun observeDebugInfo(transaction: RunnerTransaction) {
        transactionViewModel.loadDebugInfo(transaction)
        transactionViewModel.debugInfoMutableLiveData.observe(viewLifecycleOwner) { info ->
            info?.let { setDebugInfoAdapter(it) }
        }
    }

    private fun observeTransactionMessages(transaction: RunnerTransaction) {
        transactionViewModel.loadTransactionMessages(transaction)
        transactionViewModel.messagesInfoLiveData.observe(viewLifecycleOwner) { messages ->
                messages?.let { setMessagesAdapter(it) }
            }
    }

    private fun setAboutInfoAdapter(transactionDetailsInfo: List<TransactionDetailsInfo>) {
            aboutInfoAdapter = TransactionDetailsRecyclerAdapter(transactionDetailsInfo, this, this, true)
            aboutInfoRecyclerView.adapter = aboutInfoAdapter
    }

    private fun setDeviceInfoAdapter(transactionDetailsInfo: List<TransactionDetailsInfo>) {
        deviceInfoAdapter = TransactionDetailsRecyclerAdapter(transactionDetailsInfo, this, this)
        deviceInfoRecyclerView.adapter = deviceInfoAdapter
        deviceInfoRecyclerView.visibility = View.VISIBLE
    }

    private fun setDebugInfoAdapter(transactionDetailsInfo: List<TransactionDetailsInfo>) {
        debugInfoAdapter = TransactionDetailsRecyclerAdapter(transactionDetailsInfo, this, this)
        debugInfoRecyclerView.adapter = debugInfoAdapter
        debugInfoRecyclerView.visibility = View.VISIBLE
    }

    private fun setMessagesAdapter(transactionDetailsMessages: List<TransactionDetailsMessages>) {
        messagesInfoAdapter = TransactionMessagesRecyclerAdapter(transactionDetailsMessages)
        messagesInfoRecyclerView.adapter = messagesInfoAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActionItemClick(actionId: String, titleTextView: View) {
       transactionNavigationInterface.navActionDetails(actionId, titleTextView)
    }

    override fun onParserItemClicked(id: String) {
        transactionNavigationInterface.navParserDetailsFragment(aboutInfoAdapter.getActionId(),  id.toInt())
    }
}