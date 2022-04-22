package com.hover.runner.filters

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hover.runner.actions.ActionRepo
import com.hover.runner.transactions.TransactionsRepo
import com.hover.runner.utils.Utils
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.database.HoverRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class FilterViewModel(private val application: Application, private val actionRepo: ActionRepo) : ViewModel() {
	var filterQuery: MediatorLiveData<SimpleSQLiteQuery> = MediatorLiveData()

	val searchString: MutableLiveData<String> = MutableLiveData()
	val selectedTags: MutableLiveData<List<String>> = MutableLiveData()

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
		}
	}

	abstract fun onTransactionUpdate()

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

	abstract fun generateSQLStatement(search: String?)

	abstract fun generateSQLStatement(tagList: List<String>?)

	override fun onCleared() {
		try {
			LocalBroadcastManager.getInstance(application).unregisterReceiver(transactionReceiver)
		} catch (ignored: Exception) { }
		super.onCleared()
	}
}