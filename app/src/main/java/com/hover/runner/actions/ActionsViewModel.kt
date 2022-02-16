package com.hover.runner.actions

import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hover.runner.database.ActionRepo
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

const val SQL_SELECT = "SELECT * FROM hover_actions"

class ActionsViewModel(private val actionRepo: ActionRepo) : ViewModel() {

	val allActions: LiveData<List<HoverAction>> = actionRepo.getAll()
	val filteredActions: MediatorLiveData<List<HoverAction>> = MediatorLiveData()

	var filterQuery: MediatorLiveData<SimpleSQLiteQuery> = MediatorLiveData()

	val searchString: MutableLiveData<String> = MutableLiveData()
	val selectedTags: MutableLiveData<List<String>> = MutableLiveData()

	init {
		filteredActions.value = listOf()
		filteredActions.apply {
			addSource(allActions, this@ActionsViewModel::runFilter)
			addSource(filterQuery, this@ActionsViewModel::runFilter)
		}

		filterQuery.value = null
		filterQuery.apply {
			addSource(searchString, this@ActionsViewModel::generateSQLStatement)
			addSource(selectedTags, this@ActionsViewModel::generateSQLStatement)
		}

		selectedTags.value = listOf()
		viewModelScope.launch(Dispatchers.IO) {
			selectedTags.postValue(actionRepo.getAllTags())
		}
	}

	private fun runFilter(actions: List<HoverAction>?) {
		if (!actions.isNullOrEmpty() && filterQuery.value == null)
			filteredActions.value = actions
	}

	private fun runFilter(query: SimpleSQLiteQuery?) {
		filteredActions.value = if (query != null) actionRepo.search(query)	else allActions.value
	}

	fun reset() {
		searchString.value = null
		selectedTags.value = actionRepo.getAllTags()
	}

	fun setSearch(value: String) {
		viewModelScope.launch(Dispatchers.IO) {
			searchString.postValue(value)
		}
	}

	fun getAllTags(): LiveData<List<String>> {
		return liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
			emit(actionRepo.getAllTags())
		}
	}

	fun setTags(value: List<String>) {
		viewModelScope.launch(Dispatchers.IO) {
			selectedTags.postValue(value)
		}
	}

	private fun generateSQLStatement(search: String?) {
		filterQuery.postValue(actionRepo.generateSQLStatement(search, selectedTags.value))
	}

	private fun generateSQLStatement(tagList: List<String>?) {
		filterQuery.postValue(actionRepo.generateSQLStatement(searchString.value, tagList))
	}
}