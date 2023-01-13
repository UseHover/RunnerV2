package com.hover.runner.testRunCreation

import android.app.Application
import androidx.lifecycle.*
import com.hover.runner.actions.ActionRepo
import com.hover.runner.testRuns.TestRun
import com.hover.runner.testRuns.TestRunRepo
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.Utils
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewRunViewModel(private val application: Application, private val actionRepo: ActionRepo, private val runRepo: TestRunRepo) : ViewModel() {
	var actionQueue: MutableLiveData<List<HoverAction>> = MutableLiveData()

	var unfilledActions: MediatorLiveData<List<HoverAction>> = MediatorLiveData()
	var run: MutableLiveData<TestRun> = MutableLiveData()

	var startHour: Int = 0
	var startMin: Int = 0
	var startTimestamp: MutableLiveData<Long> = MutableLiveData()

	init {
		actionQueue.value = listOf()
		startTimestamp.value = 0L
		unfilledActions.addSource(actionQueue, this@NewRunViewModel::initUnfilled)
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
				val keys: Iterator<String> = a.required_params.keys()
				while (keys.hasNext()) {
					val key = keys.next()
					if (SharedPrefUtils.getVarValue(a.public_id, key, application).isEmpty()) {
						unfilled.add(a)
						break
					}
				}
			}
			unfilledActions.postValue(unfilled)
		}
	}

	fun next(): Boolean {
		val a = unfilledActions.value!![0]
		val keys: Iterator<String> = a.required_params.keys()
		while (keys.hasNext()) {
			val key = keys.next()
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

	fun save(name: String, freq: Int) {
		val r = TestRun(name, freq, if (startTimestamp.value == 0L) System.currentTimeMillis() else startTimestamp.value!!, Utils.convertActionListToIds(actionQueue.value!!))
		viewModelScope.launch(Dispatchers.IO) {
			val id = runRepo.saveNew(r)
			run.postValue(runRepo.load(id))
		}
	}

	fun setTime(h: Int, m: Int) {
		startHour = h
		startMin = m
	}

	fun setTimestamp(stamp: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			startTimestamp.postValue(stamp)
		}
	}

	fun clear() {
		startHour = 0
		startMin = 0
		startTimestamp.postValue(0L)
		actionQueue.postValue(listOf())
		unfilledActions.postValue(null)
		run.postValue(null)
	}
}