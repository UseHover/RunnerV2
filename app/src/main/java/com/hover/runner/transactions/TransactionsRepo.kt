package com.hover.runner.transactions

import androidx.lifecycle.LiveData
import com.hover.sdk.database.HoverRoomDatabase
import com.hover.sdk.transactions.Transaction

class TransactionsRepo(private val sdkDB: HoverRoomDatabase) {
	fun getAllTransactions(): LiveData<List<Transaction>> {
		return sdkDB.transactionDao().allLive
	}

	fun getAllTransactionsByParser(parserId: Int): LiveData<List<Transaction>> {
		return sdkDB.transactionDao().getLiveTransactionsByParserId("%$parserId%");
	}

	fun loadStatusForAction(actionId: String?): String? {
		return sdkDB.transactionDao().getLatestStatusForAction(actionId)
	}

	fun getTransaction(uuid: String): LiveData<Transaction> {
		return sdkDB.transactionDao().getLiveTransaction(uuid)
	}

	fun getTransactions(uuids: Array<String>): LiveData<List<Transaction>> {
		return sdkDB.transactionDao().getLiveTransactions(uuids)
	}

	fun getLiveTransactionsByAction(actionId: String): LiveData<List<Transaction>> {
		return sdkDB.transactionDao().getLiveTransactionsByActionId(actionId);
	}
}