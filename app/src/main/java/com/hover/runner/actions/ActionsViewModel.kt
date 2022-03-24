package com.hover.runner.actions

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hover.runner.transactions.TransactionsRepo
import com.hover.runner.utils.Utils
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.database.HoverRoomDatabase
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ActionsViewModel(private val application: Application, private val actionRepo: ActionRepo, private val transactionsRepo: TransactionsRepo) : ViewModel() {

	val allActions: LiveData<List<HoverAction>> = actionRepo.getAll()
	val filteredActions: MediatorLiveData<List<HoverAction>> = MediatorLiveData()

	val statuses: MediatorLiveData<HashMap<String, String?>> = MediatorLiveData()
	private var filterQuery: MediatorLiveData<SimpleSQLiteQuery> = MediatorLiveData()

	val searchString: MutableLiveData<String> = MutableLiveData()
	val selectedTags: MutableLiveData<List<String>> = MutableLiveData()
	val selectedStatuses: MutableLiveData<List<String?>> = MutableLiveData()

	private val transactionReceiver: BroadcastReceiver

	init {
		transactionReceiver = object : BroadcastReceiver() {
			override fun onReceive(context: Context?, intent: Intent?) {
				viewModelScope.launch {
					lookUpStatuses(allActions.value)
				}
			}
		}
		LocalBroadcastManager.getInstance(application)
			.registerReceiver(transactionReceiver, IntentFilter(Utils.getPackage(application).plus(".TRANSACTION_UPDATE")))

		filteredActions.value = listOf()

		filteredActions.apply {
			addSource(allActions, this@ActionsViewModel::runFilter)
			addSource(filterQuery, this@ActionsViewModel::runFilter)
			addSource(selectedStatuses) { runFilter(filterQuery.value) }
		}
		statuses.addSource(allActions, this@ActionsViewModel::lookUpStatuses)

		filterQuery.value = null
		filterQuery.apply {
			addSource(searchString, this@ActionsViewModel::generateSQLStatement)
			addSource(selectedTags, this@ActionsViewModel::generateSQLStatement)
		}
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

	private fun lookUpStatuses(actions: List<HoverAction>?) {
		actions?.let {
			val sMap = hashMapOf<String, String?>()
			HoverRoomDatabase.getInstance(application).runInTransaction {
				for (action in actions)
					sMap[action.public_id] = transactionsRepo.loadStatusForAction(action.public_id)
			}
			statuses.postValue(sMap)
		}
	}

	fun reset() {
		searchString.value = null
		selectedTags.value = actionRepo.getAllTags()
		selectedStatuses.value = listOf("succeeded", "pending", "failed", null)
	}

	fun setSearch(value: String) {
		viewModelScope.launch(Dispatchers.IO) {
			searchString.postValue(value)
		}
	}

	fun setStatus(value: String?, shouldBePresent: Boolean) {
		viewModelScope.launch(Dispatchers.IO) {
			val selStatuses = selectedStatuses.value!!.toMutableList()
			if (shouldBePresent) selStatuses.add(value) else selStatuses.remove(value)
			selectedStatuses.postValue(selStatuses)
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

	override fun onCleared() {
		try {
			LocalBroadcastManager.getInstance(application).unregisterReceiver(transactionReceiver)
		} catch (ignored: Exception) { }
		super.onCleared()
	}
}