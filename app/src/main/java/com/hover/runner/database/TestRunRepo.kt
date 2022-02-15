package com.hover.runner.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.hover.runner.newRun.TestRun
import com.hover.runner.utils.SharedPrefUtils
import timber.log.Timber

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

	suspend fun saveNew(run: TestRun, context: Context): Long {
		SharedPrefUtils.saveQueue(run.action_id_list, context)
		return runDao.insert(run)
	}
}