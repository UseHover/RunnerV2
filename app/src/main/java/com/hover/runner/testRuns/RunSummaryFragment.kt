package com.hover.runner.testRuns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hover.runner.R
import com.hover.runner.actions.ActionsViewModel
import com.hover.runner.actions.adapters.VariableRecyclerAdapter
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.RunSummaryFragmentLayoutBinding
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class RunSummaryFragment : BaseFragment() {
	private var _binding: RunSummaryFragmentLayoutBinding? = null
	private val binding get() = _binding!!

	private val actionsViewModel: ActionsViewModel by sharedViewModel()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).DARK)
		_binding = RunSummaryFragmentLayoutBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initListeners()
		initObservers()
		setupStart()
	}

	private fun initListeners() {
		binding.runTitle.setOnClickListener { navigateBack() }
	}

	private fun initObservers() {
		actionsViewModel.variableList.observe(viewLifecycleOwner) {
			it?.let { onVarUpdate(it) }
		}
	}

	private fun onVarUpdate(vars: List<String>) {
		binding.runTitle.text = resources.getString(R.string.run_head, vars.size)
		val adapter = VariableRecyclerAdapter(vars)
		binding.actionVariables.setLayoutManagerToLinear()
		binding.actionVariables.adapter = adapter
	}

	private fun setupStart() {
		binding.startBtn.setOnClickListener {
			if (actionsViewModel.incompleteActions.value!!.size > 0)
				actionsViewModel.incompleteActions
			else
				UIHelper.flashMessage(requireContext(), getString(R.string.summary_incomplete))
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}