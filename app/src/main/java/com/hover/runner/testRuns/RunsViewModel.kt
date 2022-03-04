package com.hover.runner.testRuns

import android.app.Application
import androidx.lifecycle.*
import com.hover.runner.actions.ActionRepo
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.TransactionApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RunsViewModel(private val application: Application, private val runRepo: TestRunRepo, private val actionRepo: ActionRepo) : ViewModel() {
	var runs: LiveData<List<TestRun>> = runRepo.getFuture()

	var run: MutableLiveData<TestRun> = MutableLiveData()
	var actions: LiveData<List<HoverAction>>

	// This is not great, but easier than creating a whole new model just to hold a status.
	val statuses: MediatorLiveData<HashMap<String, String?>> = MediatorLiveData()

	init {
		actions = Transformations.map(run, this::getActions)
		statuses.addSource(actions, this@RunsViewModel::lookUpStatuses)
	}

	fun load(id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			run.postValue(runRepo.load(id))
		}
	}

	private fun getActions(r: TestRun): List<HoverAction> {
		return actionRepo.getHoverActions(r.action_id_list.toTypedArray())
	}

	private fun lookUpStatuses(actions: List<HoverAction>?) {
		actions?.let {
			val sMap = hashMapOf<String, String?>()
			for (action in actions)
				sMap[action.public_id] = TransactionApi.getStatusForAction(action.public_id, application)
			statuses.postValue(sMap)
		}
	}

	fun deleteRun() {
		viewModelScope.launch(Dispatchers.IO) {
			run.value?.let { runRepo.delete(it, application) }
		}
	}
}