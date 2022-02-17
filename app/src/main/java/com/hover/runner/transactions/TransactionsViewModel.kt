package com.hover.runner.transactions

import androidx.lifecycle.*
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionsViewModel(private val repo: TransactionsRepo) : ViewModel() {
	val transactions: MutableLiveData<List<Transaction>> = MutableLiveData()

	init {
		reload()
	}

	fun reload() {
		viewModelScope.launch(Dispatchers.IO) {
			transactions.postValue(repo.getAllTransactions())
		}
	}
}