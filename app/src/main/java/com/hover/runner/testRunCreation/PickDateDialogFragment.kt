package com.hover.runner.testRunCreation

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

	private val newRunViewModel: NewRunViewModel by sharedViewModel()

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val c = Calendar.getInstance()
		val year = c.get(Calendar.YEAR)
		val month = c.get(Calendar.MONTH)
		val day = c.get(Calendar.DAY_OF_MONTH)

		return DatePickerDialog(requireActivity(), this, year, month, day)
	}

	override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
		var cal = Calendar.getInstance()
		cal.set(Calendar.YEAR, year)
		cal.set(Calendar.MONTH, month)
		cal.set(Calendar.DAY_OF_MONTH, day)
		cal.set(Calendar.HOUR_OF_DAY, newRunViewModel.startHour)
		cal.set(Calendar.MINUTE, newRunViewModel.startMin)
		newRunViewModel.setTimestamp(cal.timeInMillis)
	}
}