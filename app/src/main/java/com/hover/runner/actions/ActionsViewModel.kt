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

	private val transactionReceiver: BroadcastReceiver

	init {
		transactionReceiver = object : BroadcastReceiver() {
			override fun onReceive(context: Context?, intent: Intent?) {
				viewModelScope.launch {
					lookUpStatuses(filteredActions.value)
				}
			}
		}
		LocalBroadcastManager.getInstance(application)
			.registerReceiver(transactionReceiver, IntentFilter(Utils.getPackage(application).plus(".TRANSACTION_UPDATE")))

		filteredActions.value = listOf()

		filteredActions.apply {
			addSource(allActions, this@ActionsViewModel::runFilter)
			addSource(filterQuery, this@ActionsViewModel::runFilter)
		}
		statuses.addSource(filteredActions, this@ActionsViewModel::lookUpStatuses)

		filterQuery.value = null
		filterQuery.apply {
			addSource(searchString, this@ActionsViewModel::generateSQLStatement)
			addSource(selectedTags, this@ActionsViewModel::generateSQLStatement)
		}
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

	override fun onCleared() {
		try {
			LocalBroadcastManager.getInstance(application).unregisterReceiver(transactionReceiver)
		} catch (ignored: Exception) { }
		super.onCleared()
	}
}