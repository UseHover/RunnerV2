package com.hover.runner.actions

import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hover.runner.database.ActionRepo
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ActionsViewModel(private val actionRepo: ActionRepo) : ViewModel() {

	val allActions: LiveData<List<HoverAction>> = actionRepo.getAll()
	val filteredActions: MediatorLiveData<List<HoverAction>> = MediatorLiveData()

	val filterQuery: MutableLiveData<SimpleSQLiteQuery> = MutableLiveData()

	init {
		filteredActions.value = listOf()
		filteredActions.apply {
			addSource(allActions, this@ActionsViewModel::runFilter)
			addSource(filterQuery, this@ActionsViewModel::runFilter)
		}
	}

	private fun runFilter(actions: List<HoverAction>?) {
		if (!actions.isNullOrEmpty() && filterQuery.value == null)
			filteredActions.value = actions
	}

	private fun runFilter(query: SimpleSQLiteQuery?) {
		filteredActions.value = if (query != null) actionRepo.search(query)	else allActions.value
	}

	fun setFilter(query: SimpleSQLiteQuery?) {
		viewModelScope.launch(Dispatchers.IO) {
			filterQuery.postValue(query)
		}
	}
}