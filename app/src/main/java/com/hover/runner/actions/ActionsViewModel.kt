package com.hover.runner.actions

import android.app.Application
import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hover.runner.transactions.TransactionsRepo
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

const val SQL_SELECT = "SELECT * FROM hover_actions"

class ActionsViewModel(private val application: Application, private val actionRepo: ActionRepo, private val transactionsRepo: TransactionsRepo) : ViewModel() {

	val allActions: LiveData<List<HoverAction>> = actionRepo.getAll()
	val filteredActions: MediatorLiveData<List<HoverAction>> = MediatorLiveData()

	private val transactionsByDistinctActions = transactionsRepo.getTransactionByDistinctActions()
	val statuses: MediatorLiveData<HashMap<String, String?>> = MediatorLiveData()

	private var filterQuery: MediatorLiveData<SimpleSQLiteQuery> = MediatorLiveData()

	val searchString: MutableLiveData<String> = MutableLiveData()
	val selectedTags: MutableLiveData<List<String>> = MutableLiveData()

	init {
		filteredActions.value = listOf()

		filteredActions.apply {
			addSource(allActions, this@ActionsViewModel::runFilter)
			addSource(filterQuery, this@ActionsViewModel::runFilter)
		}

		statuses.addSource(filteredActions, this@ActionsViewModel::lookUpStatuses)
		statuses.addSource(transactionsByDistinctActions, this@ActionsViewModel::updateStatuses)

		filterQuery.value = null
		filterQuery.apply {
			addSource(searchString, this@ActionsViewModel::generateSQLStatement)
			addSource(selectedTags, this@ActionsViewModel::generateSQLStatement)
		}
	}

	fun filterOutActions(status: String?) {
		val actionIdsByStatus : Array<String> =  statuses.value!!.entries.filter { it.value == status }.map { it.key }.toTypedArray()
		val actions = actionRepo.getHoverActions(actionIdsByStatus);
		filteredActions.postValue(actions)
	}


	private fun runFilter(actions: List<HoverAction>?) {
		if (!actions.isNullOrEmpty() && filterQuery.value == null) {
			filteredActions.value = actions
			selectedTags.postValue(actionRepo.getAllTags())
		}
	}

	private fun runFilter(query: SimpleSQLiteQuery?) {
		filteredActions.value = if (query != null) actionRepo.search(query)	else allActions.value
	}

	private fun updateStatuses(transactions: List<Transaction>) {
		Timber.i("Update status has been called")

		val actions: List<HoverAction>? = filteredActions.value
		actions?.let {
			val sMap = hashMapOf<String, String?>()
			for (action in actions)
				sMap[action.public_id] = transactions.find { it.actionId == action.public_id }?.status
			statuses.postValue(sMap)
		}
	}

	private fun lookUpStatuses(actions: List<HoverAction>?) {
		actions?.let {
			val sMap = hashMapOf<String, String?>()
			for (action in actions)
				sMap[action.public_id] = transactionsByDistinctActions.value!!.find { it.actionId == action.public_id }?.status
			statuses.postValue(sMap)
		}
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
		search?.let { filterQuery.postValue(actionRepo.generateSQLStatement(search, selectedTags.value)) }
	}

	private fun generateSQLStatement(tagList: List<String>?) {
		tagList?.let {  filterQuery.postValue(actionRepo.generateSQLStatement(searchString.value, tagList)) }
	}
}