package com.hover.runner.testRuns

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class RunsViewModel(private val application: Application, runRepo: TestRunRepo) : ViewModel() {
	var runs: LiveData<List<TestRun>> = runRepo.getAll()
}