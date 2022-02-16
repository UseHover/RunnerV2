package com.hover.runner.testRuns

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hover.runner.testRuns.TestRun

@Dao
interface RunDao {
	@Query("SELECT * FROM test_runs")
	fun allRuns(): LiveData<List<TestRun>>

	@Query("SELECT * FROM test_runs WHERE id = :id LIMIT 1")
	fun load(id: Long): TestRun

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun insert(run: TestRun?): Long

	@Update
	fun update(run: TestRun?)
}