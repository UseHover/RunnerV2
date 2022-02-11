package com.hover.runner.testRuns

import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import androidx.room.*
import com.hover.runner.settings.SettingsFragment
import com.hover.sdk.api.HoverParameters
import com.hover.runner.database.Converters
import org.json.JSONArray

const val ONCE = 0
const val HOURLY = 1
const val DAILY = 2
const val WEEKLY = 3
const val BIWEEKLY = 4
const val MONTHLY = 5

@TypeConverters(Converters::class)
@Entity(tableName = "test_runs")
data class TestRun(
	@NonNull
	val action_id_list: List<String>,

	@ColumnInfo(name = "frequency")
	val frequency: Int = ONCE

) : Comparable<TestRun> {

	@PrimaryKey(autoGenerate = true)
	var id: Int = 0

	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	var started_at: Long = System.currentTimeMillis()

	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	var finished_at: Long? = null


//	fun generateName(context: Context) : String {
//		if (pending_action_id_list.length() > 1)
//			return context.getString(R.string.test_run_name, pending_action_id_list, started_at)
//		else
//			return context.getString(R.string.test_run_name_single, pending_action_id_list, started_at)
//	}

	fun generateIntent(context: Context): Intent {
		//		val actionExtras: Map<String, String> = ActionVariablesCache.get(this, actionId).actionMap
		val builder = HoverParameters.Builder(context)
		builder.request(action_id_list[0] as String?)
		builder.setEnvironment(SettingsFragment.getCurrentEnv(context))
		//		actionExtras.keys.forEach { builder.extra(it, actionExtras[it]) }
		return builder.buildIntent()
	}

	override fun compareTo(other: TestRun): Int = (started_at - other.started_at).toInt()
}