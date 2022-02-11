package com.hover.runner.testRuns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.hover.runner.R
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.FragmentRunSummaryBinding
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.Utils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RunSummaryFragment : BaseFragment() {
	private var _binding: FragmentRunSummaryBinding? = null
	private val binding get() = _binding!!

	private val runViewModel: RunViewModel by sharedViewModel()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentRunSummaryBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
		(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
		binding.fab.setOnClickListener { saveAndStart() }

		runViewModel.actionQueue.observe(viewLifecycleOwner) {
			it?.let {
				if (runViewModel.actionQueue.value?.size == 1) {
					binding.toolbar.title = getString(R.string.run_single_head, it[0].name)
					binding.toolbar.subtitle = getString(R.string.run_single_head, it[0].name)
				} else
					binding.toolbar.subtitle = getString(R.string.run_single_head, it[0].name)
			}
		}

		ArrayAdapter.createFromResource(requireContext(), R.array.schedule_array, android.R.layout.simple_spinner_item).also { adapter ->
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
			binding.scheduleDropdown.adapter = adapter
		}
	}

	private fun saveAndStart() {
		TestRun(Utils.convertActionListToIds(runViewModel.actionQueue.value!!), binding.scheduleDropdown.selectedItemPosition)
		UIHelper.flashMessage(requireContext(), "Starting..")
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}