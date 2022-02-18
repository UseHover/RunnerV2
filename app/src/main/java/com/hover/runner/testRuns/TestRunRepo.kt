package com.hover.runner.testRuns

import androidx.lifecycle.LiveData
import com.hover.runner.database.AppDatabase

class TestRunRepo(db: AppDatabase) {
	private val runDao: RunDao = db.runDao()

	fun getAll(): LiveData<List<TestRun>> {
		return runDao.allRuns()
	}

	fun getFuture(): LiveData<List<TestRun>> {
		return runDao.getFuture()
	}

	fun load(id: Long): TestRun {
		return runDao.load(id)
	}

	fun update(run: TestRun) {
		runDao.update(run)
	}

	fun saveNew(run: TestRun): Long {
		return runDao.insert(run)
	}
}