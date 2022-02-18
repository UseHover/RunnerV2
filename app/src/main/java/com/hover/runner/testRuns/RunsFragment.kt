package com.hover.runner.testRuns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hover.runner.databinding.FragmentTestRunsBinding
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RunsFragment : Fragment(), RunsRecyclerAdapter.TestRunClickListener {

	private var _binding: FragmentTestRunsBinding? = null
	private val viewModel: RunsViewModel by sharedViewModel()

	private var runsRecyclerAdapter: RunsRecyclerAdapter? = null
	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTestRunsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observeActions()
	}

	override fun onResume() {
		super.onResume()
		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).DARK)
	}

	private fun observeActions() {
		viewModel.runs.observe(viewLifecycleOwner) {
			it?.let {
				binding.recyclerView.setLayoutManagerToLinear()
				runsRecyclerAdapter = RunsRecyclerAdapter(it, this)
				binding.recyclerView.adapter = runsRecyclerAdapter
			}
		}
	}

	override fun onItemClick(testRunId: Long, titleTextView: View) {

	}
}