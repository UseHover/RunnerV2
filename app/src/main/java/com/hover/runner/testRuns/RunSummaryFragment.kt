package com.hover.runner.testRuns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hover.runner.R
import com.hover.runner.actionDetail.VariableRecyclerAdapter
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.RunSummaryFragmentLayoutBinding
import com.hover.runner.home.MainActivity
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class RunSummaryFragment : BaseFragment() {
	private var _binding: RunSummaryFragmentLayoutBinding? = null
	private val binding get() = _binding!!

	private val runViewModel: RunViewModel by sharedViewModel()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).DARK)
		_binding = RunSummaryFragmentLayoutBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		load()
		initListeners()
		initObservers()
		setupStart()
	}

	private fun load() {
		if (requireArguments().getString("action_id", "").isNotEmpty())
			runViewModel.setAction(requireArguments().getString("action_id", ""))
		else
			runViewModel.loadActionsWithFilters()
	}

	private fun initListeners() {
		binding.runTitle.setOnClickListener { navigateBack() }
	}

	private fun initObservers() {
		runViewModel.actionQueue.observe(viewLifecycleOwner) {
			binding.runTitle.text = if (it.size == 1) resources.getString(R.string.run_single_head, it[0].name) else resources.getString(R.string.run_group_head, it.size)
		}

		runViewModel.variableList.observe(viewLifecycleOwner) {
			it?.let { onVarListUpdate(it) }
		}

		runViewModel.kayValMap.observe(viewLifecycleOwner) { Timber.i("observing variable values") }
	}

	private fun onVarListUpdate(vars: List<String>) {
		val adapter = VariableRecyclerAdapter(vars)
		binding.actionVariables.setLayoutManagerToLinear()
		binding.actionVariables.adapter = adapter
	}

	private fun setupStart() {
		binding.startBtn.setOnClickListener {
			if (!runViewModel.actionQueue.value.isNullOrEmpty() && runViewModel.variableList.value?.size == runViewModel.kayValMap.value?.size) {
				val intent = TestRun(JSONArray(runViewModel.actionQueue.value?.map { it.public_id })).generateIntent(requireActivity())
				(requireActivity() as MainActivity).runAction(intent)
			} else
				UIHelper.flashMessage(requireContext(), getString(R.string.summary_incomplete))
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}