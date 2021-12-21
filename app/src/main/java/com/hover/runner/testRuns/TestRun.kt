package com.hover.runner.testRuns

import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import androidx.room.*
import com.hover.runner.settings.SettingsFragment
import com.hover.sdk.api.HoverParameters
import com.hover.sdk.database.Converters
import org.json.JSONArray

@Entity(tableName = "test_runs")
@TypeConverters(Converters::class)
data class TestRun(
	@NonNull
	var pending_action_id_list: JSONArray

) : Comparable<TestRun> {

	@PrimaryKey(autoGenerate = true)
	var id: Int = 0

	var name: String? = null

	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	var started_at: Long = System.currentTimeMillis()

	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	var finished_at: Long? = null

	@NonNull
	var completed_action_id_list: JSONArray? = null

//	fun generateName(context: Context) : String {
//		if (pending_action_id_list.length() > 1)
//			return context.getString(R.string.test_run_name, pending_action_id_list, started_at)
//		else
//			return context.getString(R.string.test_run_name_single, pending_action_id_list, started_at)
//	}

	fun generateIntent(context: Context): Intent {
		//		val actionExtras: Map<String, String> = ActionVariablesCache.get(this, actionId).actionMap
		val builder = HoverParameters.Builder(context)
		builder.request(pending_action_id_list[0] as String?)
		builder.setEnvironment(SettingsFragment.getCurrentEnv(context))
		builder.style(com.hover.runner.R.style.myHoverTheme)
		//		actionExtras.keys.forEach { builder.extra(it, actionExtras[it]) }
		return builder.buildIntent()
	}

	override fun compareTo(other: TestRun): Int = (started_at - other.started_at).toInt()
}