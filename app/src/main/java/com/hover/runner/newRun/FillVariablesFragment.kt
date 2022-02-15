package com.hover.runner.newRun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.actionDetails.VariableRecyclerAdapter
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

	private val newRunViewModel: NewRunViewModel by sharedViewModel()

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
			newRunViewModel.setAction(requireArguments().getString("action_id", ""))
		else {
			Timber.e(requireArguments().getStringArray("action_ids").toString())
			newRunViewModel.setActions(requireArguments().getStringArray("action_ids"))
		}

	}

	private fun initListeners() {
		binding.runTitle.setOnClickListener { navigateBack() }
		binding.fab.setOnClickListener { next() }
		binding.skip.setOnClickListener { newRunViewModel.skip() }
	}

	private fun initObservers() {
		newRunViewModel.actionQueue.observe(viewLifecycleOwner) {
			it?.let {
				if (newRunViewModel.actionQueue.value?.size == 1)
					binding.runTitle.text = getString(R.string.new_run_single_subtitle, it[0].name)
			}
		}

		newRunViewModel.unfilledActions.observe(viewLifecycleOwner) {
			if (!it.isNullOrEmpty()) {
				Timber.e("filling. First is %s", it[0].name)
				binding.runSubtitle.text = getString(R.string.new_run_vars_subtitle, (newRunViewModel.actionQueue.value!!.size - it.size), newRunViewModel.actionQueue.value!!.size)
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
		if (!newRunViewModel.next())
			UIHelper.flashMessage(requireContext(), getString(R.string.summary_incomplete))
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}