package com.hover.runner.actions

import android.app.Application
import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.core.util.Pair
import com.hover.runner.filters.FilterViewModel
import com.hover.runner.transactions.TransactionsRepo
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.database.HoverRoomDatabase

class ActionsViewModel(private val application: Application, private val actionRepo: ActionRepo, private val transactionsRepo: TransactionsRepo) : FilterViewModel(application, actionRepo) {

	val allActions: LiveData<List<HoverAction>> = actionRepo.getAll()
	val filteredActions: MediatorLiveData<List<HoverAction>> = MediatorLiveData()

	init {
		filteredActions.value = listOf()

		filteredActions.apply {
			addSource(allActions, this@ActionsViewModel::runFilter)
			addSource(filterQuery, this@ActionsViewModel::runFilter)
			addSource(selectedStatuses) { runFilter(filterQuery.value) }
		}
		statuses.addSource(allActions, this@ActionsViewModel::lookUpStatuses)
	}

	private fun runFilter(actions: List<HoverAction>?) {
		if (!actions.isNullOrEmpty() && filterQuery.value == null) {
			filteredActions.value = actions
			reset()
		}
	}

	private fun runFilter(query: SimpleSQLiteQuery?) {
		var queryActions = if (query != null) actionRepo.search(query) else allActions.value
		if (selectedStatuses.value?.size != 4 && !statuses.value.isNullOrEmpty())
			queryActions = queryActions?.filter { statuses.value!![it.public_id] in selectedStatuses.value!! }
		filteredActions.value = queryActions
	}

	override fun onTransactionUpdate() {
		lookUpStatuses(allActions.value)
	}

	private fun lookUpStatuses(actions: List<HoverAction>?) {
		actions?.let {
			val sMap = hashMapOf<String, String?>()
			HoverRoomDatabase.getInstance(application).runInTransaction {
				for (action in it)
					sMap[action.public_id] = transactionsRepo.loadStatusForAction(action.public_id)
			}
			statuses.postValue(sMap)
		}
	}

	override fun generateSQLStatement(search: String?) {
		search?.let { generateSQLStatement(actionRepo, search, selectedTags.value, selectedDateRange.value) }
	}

	override fun generateSQLStatement(tagList: List<String>?) {
		tagList?.let { generateSQLStatement(actionRepo, searchString.value, tagList, selectedDateRange.value) }
	}

	override fun generateSQLStatement(dateRange: Pair<Long, Long>?) {
		dateRange?.let { generateSQLStatement(actionRepo, searchString.value, selectedTags.value, dateRange) }
	}
}