package com.hover.runner.transactions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hover.runner.actions.listeners.ActionClickListener
import com.hover.runner.actions.models.Action
import com.hover.runner.customViews.detailsTopLayout.DetailScreenType
import com.hover.runner.customViews.detailsTopLayout.RunnerTopDetailsView
import com.hover.runner.databinding.TransactionDetailsFragmentBinding
import com.hover.runner.parser.ParserClickListener
import com.hover.runner.transactions.RunnerTransaction
import com.hover.runner.transactions.viewmodel.TransactionViewModel
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TransactionDetailsFragment : Fragment(), ActionClickListener, ParserClickListener {
    private var _binding: TransactionDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel : TransactionViewModel by sharedViewModel()

    private lateinit var topLayout: RunnerTopDetailsView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TransactionDetailsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        loadTransaction()
    }

    private fun initViews() {
        topLayout = binding.transactionDetailsTopLayoutId
    }
    private fun loadTransaction() {
        transactionViewModel.observeTransaction(arguments?.getString("uuid")!!).observe(viewLifecycleOwner) {
            setupTopDetailsLayout(it)

        }
    }

    private fun setupTopDetailsLayout(transaction: RunnerTransaction) {
        UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).get(transaction.getStatusColor()) )
        topLayout.setTitle(transaction.getDate()!!, transaction.status)
        topLayout.setSubTitle(transaction.uuid, transaction.status)
        topLayout.setup(transaction.status, DetailScreenType.TRANSACTION, requireActivity())
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActionItemClick(actionId: String, titleTextView: View) {
        TODO("Not yet implemented")
    }

    override fun onParserItemClicked(id: String) {
        TODO("Not yet implemented")
    }
}