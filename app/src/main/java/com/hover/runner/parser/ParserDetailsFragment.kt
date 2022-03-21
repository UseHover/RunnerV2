package com.hover.runner.parser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.main.BaseFragment
import com.hover.runner.utils.StatusUiTranslator
import com.hover.runner.databinding.ParsersFragmentBinding
import com.hover.runner.utils.TextViewUtils.Companion.underline
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ParserDetailsFragment : BaseFragment(), View.OnClickListener, StatusUiTranslator {

	private var _binding: ParsersFragmentBinding? = null
	private val binding get() = _binding!!

	private val viewModel: ParserViewModel by sharedViewModel()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = ParsersFragmentBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupToolBar()
		initViewModelData()
		observeParserInfo()
		observeTransactions()
	}

	private fun setupToolBar() {
		binding.toolbarText.setOnClickListener { navigateBack() }
		binding.toolbarText.text = arguments?.get("parser_id").toString()
	}

	private fun initViewModelData() {
		val parserId: Int = arguments?.getInt("parser_id", 0)!!
		viewModel.setParser(parserId)
	}

	private fun observeParserInfo() {
		viewModel.parser.observe(viewLifecycleOwner) { parser ->
			if (parser != null) {
				binding.responseType.text = parser.responseType
				binding.category.text = parser.category
//				binding.createdAt.text = parser.created_date
				binding.sender.text = parser.senderNumber
				binding.regex.text = parser.regex
				binding.status.text = parser.status
				binding.status.setTextColor(getColor(parser.status))
			}
		}
		viewModel.action.observe(viewLifecycleOwner) { action ->
			binding.action.underline(action.name)
			binding.actionId.underline(action.public_id)
			binding.actionId.setOnClickListener(this)
			binding.action.setOnClickListener(this)
		}
	}

	private fun observeTransactions() {
		binding.transactions.setLayoutManagerToLinear()
//		viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
//			if (transactions != null) {
//				if (transactions.isEmpty()) {
//					binding.recentHeader.text = resources.getString(R.string.zero_transactions)
//				}
//				else {
//					binding.recentHeader.text = resources.getString(R.string.recent_transactions)
//					binding.transactions.adapter = TransactionRecyclerAdapter(transactions, this)
//				}
//			}
//		}
	}

	fun onTransactionItemClicked(uuid: String) {
		findNavController().navigate(R.id.navigation_transactionDetails, bundleOf("uuid" to uuid))
	}

	override fun onClick(v: View?) {
		viewModel.action.value?.let {
			findNavController().navigate(R.id.navigation_actionDetails, bundleOf("action_id" to it.public_id))
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}