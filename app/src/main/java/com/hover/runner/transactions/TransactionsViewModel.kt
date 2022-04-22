package com.hover.runner.transactions

import android.app.Application
import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.core.util.Pair
import com.hover.runner.actions.ActionRepo
import com.hover.runner.filters.FilterViewModel
import com.hover.sdk.transactions.Transaction

class TransactionsViewModel(application: Application, actionRepo: ActionRepo, private val repo: TransactionsRepo) : FilterViewModel(application, actionRepo) {
	val allTransactions: LiveData<List<Transaction>> = repo.getAllTransactions()
	val filteredTransactions: MediatorLiveData<List<Transaction>> = MediatorLiveData()

	init {
		filteredTransactions.value = listOf()

		filteredTransactions.apply {
			addSource(allTransactions, this@TransactionsViewModel::runFilter)
			addSource(filterQuery, this@TransactionsViewModel::runFilter)
		}
	}

	private fun runFilter(transactions: List<Transaction>?) {
		if (!transactions.isNullOrEmpty() && filterQuery.value == null) {
			filteredTransactions.value = transactions
			reset()
		}
	}

	private fun runFilter(query: SimpleSQLiteQuery?) {
		filteredTransactions.value = if (query != null) repo.search(query) else allTransactions.value
	}

	override fun onTransactionUpdate() {
		TODO("Not yet implemented")
	}

	override fun generateSQLStatement(search: String?) {
		generateSQLStatement(repo, search, selectedTags.value, selectedDateRange.value)
	}

	override fun generateSQLStatement(tagList: List<String>?) {
		generateSQLStatement(repo, searchString.value, tagList, selectedDateRange.value)
	}

	override fun generateSQLStatement(dateRange: Pair<Long, Long>?) {
		generateSQLStatement(repo, searchString.value, selectedTags.value, dateRange)
	}
}