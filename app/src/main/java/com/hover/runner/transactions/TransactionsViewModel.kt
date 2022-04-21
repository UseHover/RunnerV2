package com.hover.runner.transactions

import androidx.lifecycle.*
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.parsers.HoverParser
import com.hover.sdk.transactions.Transaction

class TransactionsViewModel(repo: TransactionsRepo) : ViewModel() {
	val transactions: LiveData<List<Transaction>> = repo.getAllTransactions()
}