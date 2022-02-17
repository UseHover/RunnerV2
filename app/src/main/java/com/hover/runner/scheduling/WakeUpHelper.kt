package com.hover.runner.scheduling

import android.content.Context
import com.hover.runner.testRuns.TestRun
import com.hover.runner.testRuns.WEEKLY
import com.hover.runner.utils.SharedPrefUtils
import java.util.*


object WakeUpHelper {
	const val SOURCE = "source"
	const val FCM = "fcm"
	const val TIMER = "timer"
	const val ALARM_IN_USE = "alarm_in_use"

	fun now(): Long {
		val time: Calendar = Calendar.getInstance()
		time.timeInMillis = System.currentTimeMillis()
		time.add(Calendar.SECOND, 5)
		return time.getTimeInMillis()
	}

//	fun getScheduleTime(tr: TestRun): Long {
//		val cal: Calendar = Calendar.getInstance()
//		cal.setTimeInMillis(System.currentTimeMillis())
//		if (tr.frequency == WEEKLY) {
//			cal.add(Calendar.WEEK_OF_MONTH, 1)
//			cal.set(Calendar.DAY_OF_WEEK, tr.getDay())
//		}
//		else cal.add(Calendar.DAY_OF_YEAR, 1)
//		cal.set(Calendar.HOUR_OF_DAY, tr.getHour())
//		cal.set(Calendar.MINUTE, tr.getMin())
//		return cal.getTimeInMillis()
//	}

	fun getScheduleTime(hourlyPos: Int, numHourly: Int): Long {
		val cal: Calendar = Calendar.getInstance()
		cal.setTimeInMillis(System.currentTimeMillis()) //		cal.add(Calendar.SECOND, 10);
		cal.add(Calendar.HOUR, 1)
		cal.set(Calendar.MINUTE, hourlyPos * 60 / numHourly)
		return cal.getTimeInMillis()
	}

	val plusHour: Long
		get() {
			val cal: Calendar = Calendar.getInstance()
			cal.setTimeInMillis(System.currentTimeMillis())
			cal.add(Calendar.HOUR, 1)
			return cal.getTimeInMillis()
		}

	val plusTen: Long
		get() {
			val cal: Calendar = Calendar.getInstance()
			cal.setTimeInMillis(System.currentTimeMillis())
			cal.add(Calendar.SECOND, 10)
			return cal.getTimeInMillis()
		}

	fun lockAlarms(c: Context) {
		SharedPrefUtils.saveBool(ALARM_IN_USE, true, c)
	}

	fun alarmsLocked(c: Context): Boolean {
		return SharedPrefUtils.getSavedBool(ALARM_IN_USE, c)
	}

	fun releaseAlarms(c: Context) {
		SharedPrefUtils.saveBool(ALARM_IN_USE, false, c)
	}
}