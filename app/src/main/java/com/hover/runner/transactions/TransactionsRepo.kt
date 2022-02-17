package com.hover.runner.transactions

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hover.sdk.api.Hover
import com.hover.sdk.transactions.Transaction

class TransactionsRepo(private val context: Context) {
	fun getAllTransactions(): List<Transaction> {
		return Hover.getAllTransactions(context)
	}

	fun getTransaction(uuid: String): Transaction {
		return Hover.getTransaction(uuid, context)
	}

	fun getTransactionsByAction(actionId: String): List<Transaction> {
		return Hover.getTransactionsByActionId(actionId, context)
	}

	fun getCountByStatus(actionId: String, status: String): LiveData<Int> {
//		return transactionDao.getCountByStatus(actionId, status)
		return MutableLiveData(0)
	}
}