package com.hover.runner.testRuns

import android.app.Application
import androidx.lifecycle.*
import com.hover.runner.actions.ActionRepo
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.Utils
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewRunViewModel(private val application: Application, private val actionRepo: ActionRepo, private val runRepo: TestRunRepo) : ViewModel() {
	var actionQueue: MutableLiveData<List<HoverAction>> = MutableLiveData()

	var unfilledActions: MediatorLiveData<List<HoverAction>> = MediatorLiveData()
	var run: MediatorLiveData<TestRun> = MediatorLiveData()

	init {
		actionQueue.value = listOf()
		unfilledActions.addSource(actionQueue, this@NewRunViewModel::initUnfilled)
		run.addSource(actionQueue, this@NewRunViewModel::initRun)
	}

	fun setAction(id: String) {
		viewModelScope.launch(Dispatchers.IO) {
			actionQueue.postValue(listOf(actionRepo.load(id)))
		}
	}

	fun setActions(ids: Array<String>?) {
		viewModelScope.launch(Dispatchers.IO) {
			actionQueue.postValue(ids?.let { actionRepo.getHoverActions(it) })
		}
	}

	private fun initUnfilled(actions: List<HoverAction>?) {
		val unfilled = mutableListOf<HoverAction>()
		if (!actions.isNullOrEmpty()) {
			for (a in actions) {
				for (key in a.requiredParams) {
					if (SharedPrefUtils.getVarValue(a.public_id, key, application).isEmpty()) {
						unfilled.add(a)
						break
					}
				}
			}
			unfilledActions.postValue(unfilled)
		}
	}

	private fun initRun(actions: List<HoverAction>) {
		run.postValue(TestRun(Utils.convertActionListToIds(actions)))
	}

	fun next(): Boolean {
		val a = unfilledActions.value!![0]
		for (key in a.requiredParams) {
			if (SharedPrefUtils.getVarValue(a.public_id, key, application).isEmpty())
				return false
		}
		unfilledActions.postValue(unfilledActions.value!!.slice(1 until unfilledActions.value!!.size))
		return true
	}

	fun skip() {
		val a = unfilledActions.value!![0]
		val newQueue = actionQueue.value!!.toMutableList()
		newQueue.remove(a)
		actionQueue.postValue(newQueue)
	}

	fun save(name: String, freq: Int): LiveData<Long> {
		val  r = run.value
		r!!.name = name
		r.frequency = freq
		return liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
			emit(runRepo.saveNew(r, application))
		}
	}
}