package com.hover.runner.filters

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.core.util.Pair
import com.hover.runner.actions.ActionRepo
import com.hover.runner.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class FilterViewModel(private val application: Application, private val actionRepo: ActionRepo) : ViewModel() {
	var filterQuery: MediatorLiveData<SimpleSQLiteQuery> = MediatorLiveData()

	val searchString: MutableLiveData<String> = MutableLiveData()
	val selectedTags: MutableLiveData<List<String>> = MutableLiveData()
	val selectedDateRange: MutableLiveData<Pair<Long, Long>?> = MutableLiveData()

	val statuses: MediatorLiveData<HashMap<String, String?>> = MediatorLiveData()
	val selectedStatuses: MutableLiveData<List<String?>> = MutableLiveData()

	private val transactionReceiver: BroadcastReceiver

	init {
		transactionReceiver = object : BroadcastReceiver() {
			override fun onReceive(context: Context?, intent: Intent?) {
				viewModelScope.launch {
					onTransactionUpdate()
				}
			}
		}
		LocalBroadcastManager.getInstance(application)
			.registerReceiver(transactionReceiver, IntentFilter(Utils.getPackage(application).plus(".TRANSACTION_UPDATE")))

		filterQuery.value = null
		filterQuery.apply {
			addSource(searchString, this@FilterViewModel::generateSQLStatement)
			addSource(selectedTags, this@FilterViewModel::generateSQLStatement)
			addSource(selectedDateRange, this@FilterViewModel::generateSQLStatement)
		}
	}

	abstract fun onTransactionUpdate()

	fun reset() {
		searchString.value = null
		selectedTags.value = actionRepo.getAllTags()
		selectedDateRange.value = null
		selectedStatuses.value = listOf("succeeded", "pending", "failed", null)
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

	fun setDateRange(start: Long, end: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			selectedDateRange.postValue(Pair(start, end))
		}
	}

	fun setStatus(value: String?, shouldBePresent: Boolean) {
		viewModelScope.launch(Dispatchers.IO) {
			val selStatuses = selectedStatuses.value!!.toMutableList()
			if (shouldBePresent) selStatuses.add(value) else selStatuses.remove(value)
			selectedStatuses.postValue(selStatuses)
		}
	}

	abstract fun generateSQLStatement(search: String?)

	abstract fun generateSQLStatement(tagList: List<String>?)

	abstract fun generateSQLStatement(dateRange: Pair<Long, Long>?)

	fun generateSQLStatement(filterRepo: FilterRepo, search: String?, tagList: List<String>?, dateRange: Pair<Long, Long>?) {
		filterQuery.postValue(filterRepo.generateSQLStatement(search, tagList, dateRange))
	}

	override fun onCleared() {
		try {
			LocalBroadcastManager.getInstance(application).unregisterReceiver(transactionReceiver)
		} catch (ignored: Exception) { }
		super.onCleared()
	}
}