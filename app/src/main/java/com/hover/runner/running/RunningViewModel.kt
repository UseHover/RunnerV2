package com.hover.runner.running

import android.app.Application
import androidx.lifecycle.*
import com.hover.runner.actions.ActionRepo
import com.hover.runner.testRuns.ONCE
import com.hover.runner.testRuns.TestRunRepo
import com.hover.runner.testRuns.TestRun
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RunningViewModel(private val application: Application, private val actionRepo: ActionRepo, private val runRepo: TestRunRepo) : ViewModel() {

	var run: MutableLiveData<TestRun> = MutableLiveData()
	var currentAction: MediatorLiveData<HoverAction> = MediatorLiveData()

	var testRunning: MutableLiveData<Boolean> = MutableLiveData()

	init {
		testRunning.value = false
		currentAction.addSource(run, this@RunningViewModel::getFirstAction)
	}

	fun loadRun(id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			run.postValue(runRepo.load(id))
		}
	}

	private fun getFirstAction(testRun: TestRun?) {
		testRun?.let {
			currentAction.postValue(if (testRun.pending_action_id_list.isNullOrEmpty()) null else actionRepo.load(testRun.pending_action_id_list[0]))
		}
	}

	fun setRunInProgress(isIt: Boolean) {
		viewModelScope.launch(Dispatchers.IO) {
			testRunning.postValue(isIt)
		}
	}

	fun isTestRunning(): Boolean {
		return testRunning.value!!
	}

	fun updateQueue(finishedActionId: String) {
		viewModelScope.launch(Dispatchers.IO) {
			val tr = run.value!!
			val actionIdList = tr.pending_action_id_list.toMutableList()
			actionIdList.remove(finishedActionId)
			tr.pending_action_id_list = actionIdList.toList()
			if (actionIdList.size == 0 && tr.frequency != ONCE) {
				tr.finished_at = System.currentTimeMillis()
				scheduleNext(tr)
			}

			runRepo.update(tr)
			run.postValue(tr)
		}
	}

	private fun scheduleNext(tr: TestRun) {
		val newRun = TestRun(tr.name, tr.frequency, tr.getNextTime(), tr.action_id_list)
		runRepo.saveNew(newRun)
		newRun.schedule(application)
	}
}