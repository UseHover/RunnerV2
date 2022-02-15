package com.hover.runner.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hover.runner.newRun.TestRun

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