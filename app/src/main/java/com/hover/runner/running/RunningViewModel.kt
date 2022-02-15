package com.hover.runner.running

import android.app.Application
import androidx.lifecycle.*
import com.hover.runner.database.ActionRepo
import com.hover.runner.database.TestRunRepo
import com.hover.runner.newRun.TestRun
import com.hover.runner.utils.SharedPrefUtils
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RunningViewModel(private val application: Application, private val actionRepo: ActionRepo, private val runRepo: TestRunRepo) : ViewModel() {

	var run: MutableLiveData<TestRun> = MutableLiveData()

	var pendingActionIdList: MutableLiveData<List<String>> = MutableLiveData()
	var currentAction: MediatorLiveData<HoverAction> = MediatorLiveData()

	var testRunning: MutableLiveData<Boolean> = MutableLiveData()

	init {
		testRunning.value = false
		currentAction.addSource(pendingActionIdList, this@RunningViewModel::getFirstAction)

		viewModelScope.launch(Dispatchers.IO) {
			pendingActionIdList.postValue(SharedPrefUtils.getQueue(application))
		}
	}

	fun loadRun(id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			run.postValue(runRepo.load(id))
		}
	}

	private fun getFirstAction(id_list: List<String>) {
		currentAction.postValue(if (id_list.isNullOrEmpty()) null else actionRepo.load(id_list[0]))
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
			val actionIdList = SharedPrefUtils.getQueue(application)
			actionIdList?.remove(finishedActionId)
			SharedPrefUtils.saveQueue(actionIdList?.toList(), application)
			pendingActionIdList.postValue(actionIdList)
		}
	}
}