package com.hover.runner.testRuns

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.*
import com.hover.runner.database.Converters
import com.hover.runner.utils.DateUtils

@Entity(tableName = "test_runs")
@TypeConverters(Converters::class)
data class TestRun(

	@NonNull
	var action_id_list: List<String>,

) : Comparable<TestRun> {

	@PrimaryKey(autoGenerate = true)
	var id: Int = 0

	var name: String = ""

	var frequency: Int = 0

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
}