package com.hover.runner.parser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.databinding.ParsersFragmentBinding
import com.hover.runner.parser.navigation.ParserNavigationInterface
import com.hover.runner.parser.viewmodel.ParserViewModel
import com.hover.runner.transactions.adapters.TransactionRecyclerAdapter
import com.hover.runner.transactions.listeners.TransactionClickListener
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ParserDetailsFragment : Fragment(), TransactionClickListener {

    private var _binding: ParsersFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var typeText : TextView
    private lateinit var actionText : TextView
    private lateinit var actionIdText : TextView
    private lateinit var categoryText : TextView
    private lateinit var statusText : TextView
    private lateinit var createdText : TextView
    private lateinit var senderText : TextView
    private lateinit var regexText : TextView
    private lateinit var toolBarText : TextView
    private lateinit var recentTransText : TextView
    private lateinit var transactionRecyclerView: RecyclerView

    private val viewModel : ParserViewModel by sharedViewModel()
    private val parserNavigationInterface  = activity as ParserNavigationInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ParsersFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupToolBar()
        initViewModelData()
        observeParserInfo()
        observeTransactions()
    }

    private fun initViews() {
         typeText = binding.parserTypeContent
         actionText = binding.parserActionContent
         actionIdText = binding.parserActionIdContent
         categoryText = binding.parserCategoryContent
         statusText = binding.parserStatusContent
         createdText = binding.parserCreatedAtContent
         senderText = binding.parserSenderContent
         regexText = binding.parserRegexContent
         toolBarText = binding.parserDetailsToolbarText
         recentTransText = binding.recentTransaId
         transactionRecyclerView = binding.parserTransacRecyclerView
    }
    private fun setupToolBar() {
        toolBarText.setOnClickListener{activity?.onBackPressed()}
        toolBarText.text = arguments?.get("parser_id").toString()
    }
    private fun initViewModelData() {
        val parserId : Int = arguments?.getInt("parser_id", 0)!!
        viewModel.getParser(parserId)
        viewModel.getTransactions(parserId)
    }

    private fun observeParserInfo() {
        viewModel.parserLiveData.observe(viewLifecycleOwner) { parser ->
            if (parser != null) {
                UIHelper.underlineText(actionText, parser.action_name)
                UIHelper.underlineText(actionIdText, parser.action_id)

                typeText.text = parser.type
                categoryText.text = parser.category
                createdText.text = parser.created_date
                senderText.text = parser.sender
                regexText.text = parser.regex
                statusText.setText(parser.getStatusText())
                statusText.setTextColor(RunnerColor(requireContext()).get(parser.getStatusColor()))

                actionIdText.setOnClickListener { parserNavigationInterface.navActionDetails(parser.action_id, actionText) }
                actionText.setOnClickListener { parserNavigationInterface.navActionDetails(parser.action_id, it) }
            }
        }
    }
    private fun observeTransactions() {
        transactionRecyclerView.layoutManager = UIHelper.setMainLinearManagers(context)
        viewModel.transactionsLiveData.observe(viewLifecycleOwner) { transactions ->
            if(transactions !=null) {
                if(transactions.isEmpty()) {
                    recentTransText.text = resources.getString(R.string.zero_transactions)
                }
                else {
                    recentTransText.text = resources.getString(R.string.recent_transactions)
                    transactionRecyclerView.adapter = TransactionRecyclerAdapter(transactions, this)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTransactionItemClicked(uuid: String) {
        parserNavigationInterface.navTransactionDetails(uuid)
    }
}