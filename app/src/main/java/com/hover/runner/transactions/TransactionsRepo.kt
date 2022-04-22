package com.hover.runner.transactions

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hover.runner.filters.FilterRepo
import com.hover.sdk.database.HoverRoomDatabase
import com.hover.sdk.transactions.Transaction

class TransactionsRepo(private val sdkDB: HoverRoomDatabase): FilterRepo(sdkDB) {
	override fun getTable(): String { return "SELECT * FROM hover_transactions" }

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

	override fun search(sqlWhere: SimpleSQLiteQuery): List<Transaction> {
		return sdkDB.transactionDao().search(sqlWhere)
	}

	override fun generateSearchString(search: String?): String {
		if (search.isNullOrEmpty())
			return ""
		val s = "%$search%"
		return "(uuid LIKE '$s' OR actionName LIKE '$s' OR actionId like '$s')"
	}
}