package com.hover.runner.testRunCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.hover.runner.R
import com.hover.runner.databinding.FragmentRunSummaryBinding
import com.hover.runner.main.BaseFragment
import com.hover.runner.utils.DateUtils
import com.hover.runner.utils.UIHelper
import com.hover.sdk.actions.HoverAction
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.lang.Math.abs


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

		newRunViewModel.actionQueue.observe(viewLifecycleOwner) { onLoadActions(it) }
		setUpFrequencyChoices()
		setWhenChoices()
		setUpStartPicker()
		binding.fab.setOnClickListener { validate() }
	}

	private fun onLoadActions(actions: List<HoverAction>?) {
		Timber.e("Starting with %s", actions?.size ?: "null")
		if (!actions.isNullOrEmpty()) {
			if (newRunViewModel.actionQueue.value?.size == 1) {
				binding.toolbar.title = getString(R.string.new_run_single_subtitle, actions[0].name)
				binding.toolbar.subtitle = null
			} else { binding.toolbar.subtitle = getString(R.string.new_run_subtitle, actions.size) }
			binding.nameInput.setText(generateName(actions))
		}
	}

	private fun generateName(actions: List<HoverAction>) : String {
		val now = DateUtils.timestampTemplate(System.currentTimeMillis())
		return if (actions.size > 1) getString(R.string.run_template, now, actions.size)
		else getString(R.string.run_template_single, now, actions[0].public_id)
	}

	private fun setUpFrequencyChoices() {
		val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.frequency_array, R.layout.dropdown_item)
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		binding.frequencyAutocomplete.apply {
			setAdapter(adapter)
			setText(resources.getStringArray(R.array.frequency_array)[0], false)
		}
	}

	private fun setWhenChoices() {
		val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.when_array, R.layout.dropdown_item)
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		binding.whenAutocomplete.apply {
			setAdapter(adapter)
			setText(resources.getStringArray(R.array.when_array)[0], false)
			setOnItemClickListener { parent, _, position, _ -> showStartPicker(position == 1) }
		}
	}

	private fun showStartPicker(startLater: Boolean) {
		binding.scheduleStart.visibility = if (startLater) VISIBLE else GONE
		binding.fab.text = getString(if (startLater) R.string.label_schedule else R.string.label_run)
	}

	private fun setUpStartPicker() {
		binding.scheduleStart.setOnClickListener { TimePickerFragment().show(parentFragmentManager, "timePicker") }
		newRunViewModel.startTimestamp.observe(viewLifecycleOwner) {
			if (it == 0L) binding.scheduleStart.text = null
			else binding.scheduleStart.text = DateUtils.humanFriendlyDateTime(it)
		}
	}

	private fun validate() {
		if (binding.nameInput.text.toString().isEmpty())
			UIHelper.flashMessage(requireContext(), getString(R.string.notify_name_error))
		else if (binding.whenAutocomplete.text.toString() != getString(R.string.now) && newRunViewModel.startTimestamp.value == 0L)
			UIHelper.flashMessage(requireContext(), getString(R.string.notify_start_error))
		else if (binding.whenAutocomplete.text.toString() != getString(R.string.now) && inPast(newRunViewModel.startTimestamp.value!!))
			UIHelper.flashMessage(requireContext(), getString(R.string.notify_past_error))
		else {
			if (binding.whenAutocomplete.text.toString() != getString(R.string.now) && withinTenMin(newRunViewModel.startTimestamp.value!!))
				UIHelper.flashMessage(requireContext(), getString(R.string.notify_start_now))
			saveAndStart()
		}
	}

	private fun saveAndStart() {
		newRunViewModel.run.observe(viewLifecycleOwner) {
			it?.let {
				if (withinTenMin(it.start_at))
					it.start(requireActivity())
				else
					it.schedule(requireContext())
				findNavController().navigate(R.id.navigation_actions)
			}
		}
		newRunViewModel.save(binding.nameInput.text.toString(), getFreq())
	}

	private fun withinTenMin(timestamp: Long): Boolean {
		return kotlin.math.abs(System.currentTimeMillis() - timestamp) < 10*60*1000
	}

	private fun inPast(timestamp: Long): Boolean { // 10 minute grace
		return (timestamp - System.currentTimeMillis()) < 10*60*1000
	}

	private fun getFreq(): Int {
		val arr = resources.getStringArray(R.array.frequency_array)
		for ((i, item) in arr.withIndex()) {
			if (binding.frequencyAutocomplete.text.toString() == item)
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