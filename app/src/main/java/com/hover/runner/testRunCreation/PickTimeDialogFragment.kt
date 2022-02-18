package com.hover.runner.testRunCreation

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

	private val newRunViewModel: NewRunViewModel by sharedViewModel()

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val c = Calendar.getInstance()
		c.add(Calendar.MINUTE, 10)
		val hour = c.get(Calendar.HOUR_OF_DAY)
		val minute = c.get(Calendar.MINUTE)

		return TimePickerDialog(activity, this, hour, minute, true)
	}

	override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
		newRunViewModel.setTime(hourOfDay, minute)
		DatePickerFragment().show(parentFragmentManager, "datePicker")
	}
}