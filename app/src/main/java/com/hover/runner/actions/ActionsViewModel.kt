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


	init {
		filteredActions.value = listOf()
		filteredActions.apply {
			addSource(allActions, this@ActionsViewModel::runFilter)
			addSource(filterQuery, this@ActionsViewModel::runFilter)
		}

		filterQuery.value = null
		filterQuery.apply {
			addSource(searchString, this@ActionsViewModel::generateSQLStatement)
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
	}

	fun setSearch(value: String) {
		viewModelScope.launch(Dispatchers.IO) {
			searchString.postValue(value)
		}
	}

	private fun generateSQLStatement(search: String?) {
		if (search.isNullOrEmpty()) {
			filterQuery.postValue(null)
			return
		}

		var fString = "$SQL_SELECT WHERE "
		if (!search.isNullOrEmpty())
			fString += generateSearchString(search)

		Timber.e("Searching %s", fString)
		filterQuery.postValue(SimpleSQLiteQuery(fString))
	}

	private fun generateSearchString(search: String): String {
		val s = "%$search%"
		return "(server_id LIKE '$s' OR name LIKE '$s')"
	}
}