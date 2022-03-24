package com.hover.runner.testRuns

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hover.runner.testRuns.TestRun

@Dao
interface RunDao {
	@Query("SELECT * FROM test_runs ORDER BY start_at DESC")
	fun allRuns(): List<TestRun>

	@Query("SELECT * FROM test_runs WHERE finished_at = 0")
	fun getFuture(): List<TestRun>

	@Query("SELECT * FROM test_runs WHERE finished_at != 0")
	fun getPast(): List<TestRun>

	@Query("SELECT * FROM test_runs WHERE id = :id LIMIT 1")
	fun load(id: Long): TestRun

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun insert(run: TestRun?): Long

	@Update
	fun update(run: TestRun?)

	@Delete
	fun delete(run: TestRun?)
}