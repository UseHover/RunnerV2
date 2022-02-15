package com.hover.runner.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.hover.runner.testRuns.TestRun
import com.hover.runner.utils.SharedPrefUtils

class TestRunRepo(db: AppDatabase) {
	private val runDao: RunDao = db.runDao()

	fun getAll(): LiveData<List<TestRun>> {
		return runDao.allRuns()
	}

	fun update(run: TestRun) {
		runDao.update(run)
	}

	fun startNew(run: TestRun, context: Context) {
		runDao.insert(run)
		SharedPrefUtils.saveQueue(run.action_id_list, context)
	}
}