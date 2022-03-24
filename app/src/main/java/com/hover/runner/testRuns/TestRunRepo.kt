package com.hover.runner.testRuns

import android.content.Context
import androidx.lifecycle.LiveData
import com.hover.runner.database.AppDatabase

class TestRunRepo(db: AppDatabase) {
	private val runDao: RunDao = db.runDao()

	fun get(filter: String): List<TestRun> {
		return when (filter) {
			"Future" -> runDao.getFuture()
			"Past" -> runDao.getPast()
			else -> runDao.allRuns()
		}
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

	fun delete(run: TestRun, context: Context) {
		run.cancelAlarm(context)
		runDao.delete(run)
	}
}