package com.hover.runner.testRuns

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.home.BaseFragment
import com.hover.runner.databinding.FragmentRunSummaryBinding
import com.hover.runner.running.RunningActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RunSummaryFragment : BaseFragment() {

	private var _binding: FragmentRunSummaryBinding? = null
	private val binding get() = _binding!!

	private val newRunViewModel: NewRunViewModel by sharedViewModel()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentRunSummaryBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
		(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
		binding.fab.setOnClickListener { saveAndStart() }

		newRunViewModel.actionQueue.observe(viewLifecycleOwner) {
			it?.let {
				if (newRunViewModel.actionQueue.value?.size == 1) {
					binding.toolbar.title = getString(R.string.new_run_single_subtitle, it[0].name)
					binding.toolbar.subtitle = null
				} else
					binding.toolbar.subtitle = getString(R.string.new_run_subtitle, it.size)
			}
		}

		newRunViewModel.run.observe(viewLifecycleOwner) { it?.let { binding.nameInput.setText(it.generateName(requireContext())) } }

		ArrayAdapter.createFromResource(requireContext(), R.array.schedule_array, R.layout.dropdown_item).also { adapter ->
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
			binding.scheduleAutocomplete.setAdapter(adapter)
			binding.scheduleAutocomplete.setText(resources.getStringArray(R.array.schedule_array)[0], false)
			binding.scheduleAutocomplete.onItemClickListener
		}
	}

	private fun saveAndStart() {
		newRunViewModel.save(binding.nameInput.text.toString(), binding.scheduleAutocomplete.listSelection).observe(viewLifecycleOwner) {
			it?.let {
				val i = Intent(requireActivity(), RunningActivity::class.java)
				i.putExtra("runId", it)
				startActivity(i)
				findNavController().navigate(R.id.navigation_actions)
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}