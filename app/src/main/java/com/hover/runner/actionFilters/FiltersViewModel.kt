package com.hover.runner.actionFilters

import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hover.runner.database.ActionRepo
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

const val SQL_SELECT = "SELECT * FROM hover_actions"

class FiltersViewModel(private val actionRepo: ActionRepo) : ViewModel() {

	private val allActions: LiveData<List<HoverAction>> = actionRepo.getAll()
	val filteredActions: MediatorLiveData<List<HoverAction>> = MediatorLiveData()

	var filterQuery: MediatorLiveData<SimpleSQLiteQuery> = MediatorLiveData()
	val searchString: MutableLiveData<String> = MutableLiveData()


	init {
		filteredActions.value = listOf()
		filteredActions.apply {
			addSource(allActions, this@FiltersViewModel::runFilter)
			addSource(filterQuery, this@FiltersViewModel::runFilter)
		}

		filterQuery.value = null
		filterQuery.apply {
			addSource(searchString, this@FiltersViewModel::generateSQLStatement)
		}
	}

	private fun runFilter(actions: List<HoverAction>?) {
		if (!actions.isNullOrEmpty() && filterQuery.value == null)
			filteredActions.value = actions
	}

	private fun runFilter(query: SimpleSQLiteQuery?) {
		query?.let { filteredActions.value = actionRepo.search(query) }
	}

	fun setFilter(newValue: SimpleSQLiteQuery?) {
		filterQuery.postValue(newValue)
	}

	fun setSearch(value: String) {
		viewModelScope.launch(Dispatchers.IO) {
			searchString.postValue(value)
		}
	}

	private fun generateSQLStatement(search: String) {
		var fString = SQL_SELECT

		if (!search.isEmpty()) {
			fString += " WHERE "
			fString += generateSearchString(search)
		}
		Timber.e("Searching %s", fString)
		filterQuery.postValue(SimpleSQLiteQuery(fString))
	}

	private fun generateSearchString(search: String): String {
		val s = "%$search%"
		return "(server_id LIKE '$s' OR name LIKE '$s')"
	}

	fun saveFilter() {

	}
}