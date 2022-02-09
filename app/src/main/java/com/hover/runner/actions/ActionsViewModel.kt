package com.hover.runner.actions

import androidx.lifecycle.*
import com.hover.runner.database.ActionRepo
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ActionsViewModel(private val actionRepo: ActionRepo) : ViewModel() {

	val allActions: MutableLiveData<List<HoverAction>> = MutableLiveData()
	val filteredActions: MediatorLiveData<List<HoverAction>> = MediatorLiveData()

	val filterString: MutableLiveData<String> = MutableLiveData()
	val filterMap: MutableLiveData<Map<String, String>> = MutableLiveData()

	init {
		filteredActions.value = listOf()
		filteredActions.apply {
			addSource(allActions, this@ActionsViewModel::runFilter)
			addSource(filterString, this@ActionsViewModel::runFilter)
		}
		viewModelScope.launch(Dispatchers.IO) {
//			filterString = SharedPrefUtils.getSavedString()
			allActions.postValue(actionRepo.getAllActionsFromHover())
		}
	}

	private fun runFilter(actions: List<HoverAction>?) {
		if (actions != null && actions.isNotEmpty())
			runFilter(actions, filterString.value)
		else filteredActions.value = listOf<HoverAction>()
	}

	private fun runFilter(filters: String) {
		if (allActions.value != null && allActions.value!!.isNotEmpty())
			runFilter(allActions.value!!, filters)
		else filteredActions.value = listOf<HoverAction>()
	}

	private fun runFilter(actions: List<HoverAction>, filters: String?) {
		filteredActions.value = actions
	}

	fun addFilter(newKey: String, check: Boolean) {
		if (check)
			filterString.postValue("$filterString.value$newKey,")
		else
			filterString.postValue(filterString.value?.replace("$newKey,", "", ignoreCase = true))
	}

	fun setFilter(newValue: String?) {
		filterString.postValue(newValue)
	}

	fun generateFilterMap() : Map<String, String> {
//		viewModelScope.launch(Dispatchers.Default) {
			val filterMap = mutableMapOf<String, String>()
//			filterString.value?.split(",")?.map { keyValue ->
//				filterMap.put(keyValue.split(":").get(0), keyValue.split(":").get(1))
//			}
			return filterMap
//		}
	}
}