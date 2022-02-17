package com.hover.runner.testRuns

import android.content.Context
import androidx.lifecycle.LiveData
import com.hover.runner.database.AppDatabase
import com.hover.runner.utils.SharedPrefUtils

class TestRunRepo(db: AppDatabase) {
	private val runDao: RunDao = db.runDao()

	fun getAll(): LiveData<List<TestRun>> {
		return runDao.allRuns()
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