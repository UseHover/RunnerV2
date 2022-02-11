package com.hover.runner.testRuns

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.actionDetail.VariableRecyclerAdapter
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.FragmentFillVariablesBinding
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import com.hover.sdk.actions.HoverAction
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class FillVariablesFragment : BaseFragment() {
	private var _binding: FragmentFillVariablesBinding? = null
	private val binding get() = _binding!!

	private val runViewModel: RunViewModel by sharedViewModel()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).DARK)
		_binding = FragmentFillVariablesBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initListeners()
		initObservers()
		load()
	}

	private fun load() {
		if (!requireArguments().getString("action_id", "").isNullOrEmpty())
			runViewModel.setAction(requireArguments().getString("action_id", ""))
		else {
			Log.e("FILLVARFRAG", requireArguments().getStringArray("action_ids").toString())
			runViewModel.setActions(requireArguments().getStringArray("action_ids"))
		}

	}

	private fun initListeners() {
		binding.runTitle.setOnClickListener { navigateBack() }
		binding.fab.setOnClickListener { next() }
		binding.skip.setOnClickListener { runViewModel.skip() }
	}

	private fun initObservers() {
		runViewModel.actionQueue.observe(viewLifecycleOwner) {
			it?.let {
				if (runViewModel.actionQueue.value?.size == 1)
					binding.runTitle.text = getString(R.string.run_single_head, it[0].name)
			}
		}

		runViewModel.unfilledActions.observe(viewLifecycleOwner) {
			if (!it.isNullOrEmpty()) {
				Timber.e("filling. First is %s", it[0].name)
				binding.runSubtitle.text = getString(R.string.fill_vars_subhead, (runViewModel.actionQueue.value!!.size - it.size), runViewModel.actionQueue.value!!.size)
				binding.actionTitle.text = it[0].name
				binding.actionSubtitle.text = it[0].public_id
				onVarListUpdate(it[0])
				if (it.size == 1) binding.fab.text = getString(R.string.finish)
			} else if (it.isEmpty()){
				findNavController().navigate(R.id.navigation_run_summary)
			}
		}
	}

	private fun onVarListUpdate(action: HoverAction) {
		val adapter = VariableRecyclerAdapter(action)
		binding.actionVariables.setLayoutManagerToLinear()
		binding.actionVariables.adapter = adapter
	}

	private fun next() {
		if (!runViewModel.next())
			UIHelper.flashMessage(requireContext(), getString(R.string.summary_incomplete))
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}