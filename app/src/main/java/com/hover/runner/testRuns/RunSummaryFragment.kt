package com.hover.runner.testRuns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.databinding.FragmentRunSummaryBinding
import com.hover.runner.main.BaseFragment
import com.hover.runner.utils.DateUtils
import com.hover.sdk.actions.HoverAction
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


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
			Timber.e("Starting with %s", if (it == null) "null" else it.size )
			if (!it.isNullOrEmpty()) {
				if (newRunViewModel.actionQueue.value?.size == 1) {
					binding.toolbar.title = getString(R.string.new_run_single_subtitle, it[0].name)
					binding.toolbar.subtitle = null
				} else { binding.toolbar.subtitle = getString(R.string.new_run_subtitle, it.size) }
				binding.nameInput.setText(generateName(it))
			}
		}

		ArrayAdapter.createFromResource(requireContext(), R.array.schedule_array, R.layout.dropdown_item).also { adapter ->
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
			binding.scheduleAutocomplete.setAdapter(adapter)
			binding.scheduleAutocomplete.setText(resources.getStringArray(R.array.schedule_array)[0], false)
			binding.scheduleAutocomplete.onItemClickListener
		}
	}

	private fun generateName(actions: List<HoverAction>) : String {
		val now = DateUtils.timestampTemplate(System.currentTimeMillis())
		return if (actions.size > 1) getString(R.string.run_template, now, actions.size)
		else getString(R.string.run_template_single, now, actions[0].public_id)
	}

	private fun saveAndStart() {
		newRunViewModel.run.observe(viewLifecycleOwner) {
			it?.let {
				it.start(requireActivity())
				findNavController().navigate(R.id.navigation_actions)
			}
		}
		newRunViewModel.save(binding.nameInput.text.toString(), getFreq())
	}

	private fun getFreq(): Int {
		val arr = resources.getStringArray(R.array.schedule_array)
		for ((i, item) in arr.withIndex()) {
			if (binding.scheduleAutocomplete.text.toString() == item)
				return i
		}
		return 0
	}

	override fun onDestroyView() {
		super.onDestroyView()
		newRunViewModel.clear()
		_binding = null
	}
}