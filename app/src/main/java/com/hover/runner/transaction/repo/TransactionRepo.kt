package com.hover.runner.transaction.repo

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.hover.runner.database.AppDatabase
import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.sdk.transactions.TransactionContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class TransactionRepo(db: AppDatabase) {
	private val transactionDao: RunnerTransactionDao = db.runnerTransactionDao()

	suspend fun getAllTransactions(): List<RunnerTransaction> {
		return transactionDao.allTransactions()
	}

	suspend fun getTransactionsByAction(actionId: String): List<RunnerTransaction> {
		return transactionDao.transactionsByAction_Suspended(actionId)
	}

	fun getTransaction(uuid: String): LiveData<RunnerTransaction> {
		return transactionDao.getTransaction(uuid)
	}
	suspend fun getTransactions(uuids: Array<String>): List<RunnerTransaction> {
		return transactionDao.getTransactions(uuids)
	}

	suspend fun getCategories(): List<String> {
		return transactionDao.allCategories()
	}

	suspend fun getTransactionSuspended(uuid: String?): RunnerTransaction? {
		return transactionDao.getTransaction_Suspended(uuid)
	}

	fun getTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>> {
		return transactionDao.transactionsByAction(actionId, limit)
	}

	suspend fun getTransactionsByParser(parserId: Int): List<RunnerTransaction> {
		return transactionDao.transactionsByParser("%$parserId%")
	}

	suspend fun getLastTransaction(actionId: String): RunnerTransaction? {
		return transactionDao.lastTransactionsByAction_Suspended(actionId)
	}

	private fun updateTransaction(transaction: RunnerTransaction) {
		transactionDao.update(transaction)
	}

	private fun insertTransaction(transaction: RunnerTransaction) {
		transactionDao.insert(transaction)
	}

	fun insertOrUpdateTransaction(intent: Intent, context: Context) {
		Timber.i("save receiver triggered")
		AppDatabase.databaseWriteExecutor.execute {
			CoroutineScope(Dispatchers.IO).launch {
				try {
					var t: RunnerTransaction? =
						getTransactionSuspended(intent.getStringExtra(TransactionContract.COLUMN_UUID))
					if (t == null) {
						Timber.i("save receiver triggered to create new transa")
						t = RunnerTransaction.init(intent, context)
						insertTransaction(t!!)
					}
					else {
						Timber.i("save receiver triggered to update")
						t.update(intent, context)
						updateTransaction(t)
					}
				} catch (e: Exception) {
					Timber.e(e, "error")
					Timber.i("save receiver triggered has error: " + e.message)
				}
			}
		}
	}

	// For Action ID filtering
	suspend fun getActionIdsByDateRange(actionIdSubList:Array<String>, startDate : Long, endDate: Long): Array<String> {
		return transactionDao.getActionIdsByDateRange(actionIdSubList, startDate, endDate)
	}

	suspend fun getActionIdsByCategories(actionIdSubList:Array<String>, categories: Array<String>): Array<String> {
		return transactionDao.getActionIdsByCategories(actionIdSubList, categories)
	}

	suspend fun getActionIdsByTransactionSuccessful(actionIdSubList:Array<String>): Array<String> {
		return transactionDao.getActionIdsByTransactionSuccessful(actionIdSubList)
	}

	suspend fun getActionIdsByTransactionPending(actionIdSubList:Array<String>): Array<String> {
		return transactionDao.getActionIdsByTransactionPending(actionIdSubList)
	}
	suspend fun getActionIdsByTransactionFailed(actionIdSubList:Array<String>): Array<String> {
		return transactionDao.getActionIdsByTransactionFailed(actionIdSubList)
	}

	suspend fun getActionIdsWithTransactions(): List<String> {
		return transactionDao.getActionIdsWithTransactions()
	}

	// For Transaction UUID filtering
	suspend fun getUUIDsByActionIds(actionIds: Array<String>) : Array<String> {
		return transactionDao.getUUIDsByActionIds(actionIds)
	}

	suspend fun getUUIDsByDateRange(selectedUUIDs: Array<String>,  startDate : Long, endDate: Long) : Array<String> {
		return transactionDao.getTransactionsByDateRange(selectedUUIDs, startDate, endDate)
	}
	suspend fun getUUIDsByDateRange(startDate : Long, endDate: Long) : Array<String> {
		return transactionDao.getTransactionsByDateRange(startDate, endDate)
	}

	suspend fun getSuccessfulUUIDs(selectedUUIDs: Array<String>) : Array<String> {
		return transactionDao.getSuccessfulTransactions(selectedUUIDs)
	}
	suspend fun getSuccessfulUUIDs() : Array<String> {
		return transactionDao.getSuccessfulTransactions()
	}

	suspend fun getPendingUUIDs(selectedUUIDs: Array<String>) : Array<String> {
		return transactionDao.getPendingTransactions(selectedUUIDs)
	}
	suspend fun getPendingUUIDs() : Array<String> {
		return transactionDao.getPendingTransactions()
	}

	suspend fun getFailedUUIDs(selectedUUIDs: Array<String>) : Array<String> {
		return transactionDao.getFailedTransactions(selectedUUIDs)
	}
	suspend fun getFailedUUIDs() : Array<String> {
		return transactionDao.getFailedTransactions()
	}



}