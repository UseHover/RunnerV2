package com.hover.runner.testRuns

import android.app.Application
import androidx.lifecycle.*
import com.hover.runner.database.ActionRepo
import com.hover.runner.utils.SharedPrefUtils
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RunViewModel(private val application: Application, private val actionRepo: ActionRepo) : ViewModel() {

	val actionQueue: MutableLiveData<List<HoverAction>> = MutableLiveData()
	val incompleteActions: MutableLiveData<List<String>> = MutableLiveData()
	val completedActions: MutableLiveData<List<String>> = MutableLiveData()

	val variableList: LiveData<List<String>>
	val keyValMap: LiveData<Map<String, String>>

	init {
		actionQueue.value = listOf()
		variableList = Transformations.map(actionQueue, this@RunViewModel::loadAllVariables)
		keyValMap = Transformations.map(variableList, this@RunViewModel::loadVariableValues)
	}

	fun setAction(id: String) {
		viewModelScope.launch(Dispatchers.IO) {
			actionQueue.postValue(listOf(actionRepo.getHoverAction(id)))
		}
	}

	fun loadActionsWithFilters() {
		viewModelScope.launch(Dispatchers.IO) {
//			filterString = SharedPrefUtils.getSavedString()
			actionQueue.postValue(actionRepo.getAllActionsFromHover())
		}
	}

	private fun loadAllVariables(actions: List<HoverAction>): List<String> {
		val params = arrayListOf<String>()
		for (a in actions) {
			params.addAll(a.requiredParams)
		}
		return params.distinct()
	}

	private fun loadVariableValues(variableList: List<String>): Map<String, String> {
		val params = mutableMapOf<String, String>()
		for (key in variableList) {
			if (SharedPrefUtils.getVarValue(key, application).isNotEmpty())
				params[key] = SharedPrefUtils.getVarValue(key, application)
		}
		return params
	}
}