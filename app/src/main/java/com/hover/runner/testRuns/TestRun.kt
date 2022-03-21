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
import com.hover.runner.testRunning.RunningActivity
import com.hover.runner.utils.UIHelper
import timber.log.Timber
import java.util.*

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

	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	var start_at: Long,

	@NonNull
	var action_id_list: List<String>,

) : Comparable<TestRun> {

	@PrimaryKey(autoGenerate = true)
	var id: Long = 0

	var finished_at: Long = 0

	@NonNull
	var pending_action_id_list: List<String> = action_id_list

	var transaction_uuid_list: List<String> = listOf()

	override fun compareTo(other: TestRun): Int = (start_at - other.start_at).toInt()

	fun start(activity: FragmentActivity) {
		Timber.e("starting run. id: %d, freq: %d", id, frequency)
		runNow(activity)
	}

	private fun runNow(activity: FragmentActivity) {
		UIHelper.flashMessage(activity, activity.getString(R.string.notify_starting))
		val i = Intent(activity, RunningActivity::class.java)
		i.putExtra(RUN_ID, id)
		activity.startActivityForResult(i, id.toInt())
	}

	fun schedule(c: Context) {
		setAlarm(c)
		UIHelper.flashMessage(c, c.getString(R.string.notify_scheduled))
	}

	private fun setAlarm(c: Context) {
		val alarmMgr = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		alarmMgr.setExact(AlarmManager.RTC_WAKEUP, start_at,
			PendingIntent.getActivity(c, id.toInt(), generateIntent(c), PendingIntent.FLAG_CANCEL_CURRENT))
//		add "or PendingIntent.FLAG_MUTABLE" in API 31
	}

	fun cancelAlarm(c: Context) {
		val alarmMgr = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		alarmMgr.cancel(PendingIntent.getActivity(c, id.toInt(), generateIntent(c), PendingIntent.FLAG_CANCEL_CURRENT))
	}


	fun getNextTime(): Long {
		val cal: Calendar = Calendar.getInstance()
		cal.timeInMillis = start_at
		when (frequency) {
			WEEKLY -> cal.add(Calendar.WEEK_OF_MONTH, 1)
			DAILY -> cal.add(Calendar.DAY_OF_MONTH, 1)
			HOURLY -> cal.add(Calendar.HOUR_OF_DAY, 1)
		}
		return cal.timeInMillis
	}

	private fun generateIntent(c: Context): Intent {
		val i = Intent(c, RunningActivity::class.java)
		i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		i.putExtra(RUN_ID, id)
		return i
	}
}