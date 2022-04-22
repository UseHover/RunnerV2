package com.hover.runner.transactions

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.core.util.Pair
import com.hover.runner.filters.FilterRepo
import com.hover.sdk.database.HoverRoomDatabase
import com.hover.sdk.transactions.Transaction
import timber.log.Timber

class TransactionsRepo(private val sdkDB: HoverRoomDatabase): FilterRepo(sdkDB) {
	override fun getTable(): String {
		return "SELECT * FROM hover_transactions ht" + " LEFT JOIN 'hover_actions' a ON a.server_id = ht.actionId"
	}

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

	override fun generateDateString(dateRange: Pair<Long, Long>?): String {
		if (dateRange == null || (dateRange.first == 0L && dateRange.second == 0L))
			return ""

		var sqlStr = "("
		sqlStr += "reqTimestamp >= ${dateRange.first}"
		if (dateRange.second != 0L && dateRange.second > dateRange.first)
			sqlStr += " AND reqTimestamp <= ${dateRange.second}"
		sqlStr += ")"
		Timber.e("Searching date range: %s", sqlStr)
		return sqlStr
	}
}