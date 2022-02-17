package com.hover.runner.transactionDetails

import androidx.lifecycle.*
import com.hover.runner.transactions.TransactionsRepo
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionDetailsViewModel(private val repo: TransactionsRepo) : ViewModel() {
	val transaction: MutableLiveData<Transaction> = MutableLiveData()

	fun load(uuid: String) {
		viewModelScope.launch(Dispatchers.IO) {
			transaction.postValue(repo.getTransaction(uuid))
		}
	}
}