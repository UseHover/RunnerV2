package com.hover.runner.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hover.runner.testRuns.TestRun

@Dao
interface RunDao {
	@Query("SELECT * FROM test_runs")
	fun allRuns(): LiveData<List<TestRun>>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun insert(run: TestRun?)

	@Update
	fun update(run: TestRun?)
}