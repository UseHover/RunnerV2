package com.hover.runner.testRuns

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hover.runner.R
import com.hover.runner.database.Converters
import com.hover.runner.running.RunningActivity
import com.hover.runner.scheduling.WakeUpHelper
import com.hover.runner.utils.UIHelper
import timber.log.Timber

const val ONCE = 0
const val HOURLY = 1
const val DAILY = 2
const val WEEKLY = 3
const val RUN_ID = "run_id"

@Entity(tableName = "test_runs")
@TypeConverters(Converters::class)
data class TestRun(

	val name: String,

	val frequency: Int,

	@NonNull
	var action_id_list: List<String>,

) : Comparable<TestRun> {

	@PrimaryKey(autoGenerate = true)
	var id: Long = 0

	@NonNull
	var pending_action_id_list: List<String> = action_id_list

	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	var started_at: Long = System.currentTimeMillis()

	@ColumnInfo()
	var finished_at: Long = 0

	override fun compareTo(other: TestRun): Int = (started_at - other.started_at).toInt()

	fun start(activity: FragmentActivity) {
		Timber.e("starting run. id: %d, freq: %d", id, frequency)
		if (frequency == ONCE) runNow(activity)
		else schedule(activity)
	}

	private fun runNow(activity: FragmentActivity) {
		UIHelper.flashMessage(activity, activity.getString(R.string.notify_starting))
		val i = Intent(activity, RunningActivity::class.java)
		i.putExtra(RUN_ID, id)
		activity.startActivity(i)
	}

	private fun schedule(c: Context) {
		UIHelper.flashMessage(c, c.getString(R.string.notify_saved))
		setAlarm(WakeUpHelper.plusTen, c)
	}

	private fun setAlarm(time: Long, c: Context) {
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
		val i = Intent(c, RunningActivity::class.java)
		i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		i.putExtra(RUN_ID, id)
		return i
	}
}