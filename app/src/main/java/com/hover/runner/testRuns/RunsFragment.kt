package com.hover.runner.testRuns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
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
		viewModel.runs.observe(viewLifecycleOwner) { onLoad(it) }
	}

	private fun onLoad(runs: List<TestRun>?) {
		runs?.let {
			binding.recyclerView.setLayoutManagerToLinear()
			runsRecyclerAdapter = RunsRecyclerAdapter(it, this)
			binding.recyclerView.adapter = runsRecyclerAdapter
		}
	}

	override fun onItemClick(testRunId: Long, titleTextView: View) {
		findNavController().navigate(R.id.navigation_run_details, bundleOf(RUN_ID to testRunId))
	}
}