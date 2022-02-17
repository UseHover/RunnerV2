package com.hover.runner.testRuns

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import androidx.room.*
import com.hover.runner.database.Converters
import com.hover.runner.running.RunningActivity
import com.hover.runner.scheduling.WakeUpHelper
import com.hover.runner.utils.DateUtils

const val ONCE = 0
const val HOURLY = 1
const val DAILY = 2
const val WEEKLY = 3

@Entity(tableName = "test_runs")
@TypeConverters(Converters::class)
data class TestRun(

	@NonNull
	var action_id_list: List<String>,

) : Comparable<TestRun> {

	@PrimaryKey(autoGenerate = true)
	var id: Long = 0

	var name: String = ""

	var frequency: Int = 0

//	var active: Boolean = false

	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	var started_at: Long = System.currentTimeMillis()

	override fun compareTo(other: TestRun): Int = (started_at - other.started_at).toInt()

	fun generateName(context: Context) : String {
		val now = DateUtils.timestampTemplate(started_at)
		if (action_id_list.size > 1)
			return context.getString(com.hover.runner.R.string.run_template, now, action_id_list.size)
		else
			return context.getString(com.hover.runner.R.string.run_template_single, now, action_id_list[0])
	}

	fun schedule(c: Context) {
		setAlarm(WakeUpHelper.plusTen, c)
	}

	fun setAlarm(time: Long, c: Context) {
		val alarmMgr = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time,
			PendingIntent.getActivity(c, id.toInt(), generateIntent(c), PendingIntent.FLAG_CANCEL_CURRENT))
//		add "or PendingIntent.FLAG_MUTABLE" in API 31
	}

	fun getInterval(): Long {
		return when (frequency) {
			HOURLY -> AlarmManager.INTERVAL_HOUR
			DAILY -> AlarmManager.INTERVAL_DAY
			WEEKLY -> AlarmManager.INTERVAL_DAY * 7
			else -> 0
		}
	}

	private fun generateIntent(c: Context): Intent {
		val wake = Intent(c, RunningActivity::class.java)
		wake.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		wake.putExtra("runId", id)
		return wake
	}
}