package com.hover.runner.transactions.repo

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.hover.runner.database.AppDatabase
import com.hover.runner.transactions.model.RunnerTransaction
import com.hover.runner.transactions.RunnerTransactionDao
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

    suspend fun getTransactionSuspended(uuid: String): RunnerTransaction? {
        return transactionDao.getTransaction_Suspended(uuid)
    }

    fun getTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>> {
        return transactionDao.transactionsByAction(actionId, limit)
    }

   suspend fun getTransactionsByParser(parserId: Int) : List<RunnerTransaction> {
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
        AppDatabase.databaseWriteExecutor.execute {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var t : RunnerTransaction? = intent.getStringExtra(TransactionContract.COLUMN_UUID)?.let { getTransactionSuspended(it) }
                    if (t == null) {
                        t = RunnerTransaction.init(intent, context)
                        t?.let{ insertTransaction(t!!)}
                        t = getTransactionSuspended(t!!.uuid)
                    } else {
                        t.update(intent)
                        updateTransaction(t)
                    }
                    if (t != null) {
                        Timber.e("save t with uuid: %s", t.uuid)
                    };
                } catch (e: Exception) {
                    Timber.e(e, "error")
                }
            }
        }
    }
}