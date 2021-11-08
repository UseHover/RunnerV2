package com.hover.runner.transactions.usecase

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.hover.runner.database.AppDatabase
import com.hover.runner.database.DatabaseRepo
import com.hover.runner.transactions.RunnerTransaction
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.transactions.TransactionContract
import kotlinx.coroutines.*
import timber.log.Timber

class TransactionRepoInterfaceImpl(private val repo: DatabaseRepo) : TransactionRepoInterface {
    override fun getAllTransactions(): LiveData<List<RunnerTransaction>> {
        return repo.getAllTransactions()
    }

    override fun getTransaction(uuid: String): LiveData<RunnerTransaction> {
        return repo.getTransaction(uuid)
    }

    override suspend fun getTransactionSuspended(uuid: String): RunnerTransaction? {
        return repo.getTransactionSuspended(uuid)
    }

    override fun getTransactionsByAction(actionId: String): LiveData<List<RunnerTransaction>> {
        return repo.getTransactionsByAction(actionId)
    }

    override fun getTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>> {
        return repo.getTransactionsByAction(actionId, limit)
    }

    override suspend fun getLastTransaction(actionId: String): RunnerTransaction? {
        return repo.getLastTransaction(actionId)
    }

    override fun updateTransaction(transaction: RunnerTransaction) {
        repo.updateTransaction(transaction)
    }

    override fun insertTransaction(transaction: RunnerTransaction) {
        repo.insertTransaction(transaction)
    }


    override fun insertOrUpdateTransaction(intent: Intent, context: Context) {
        repo.insertOrUpdateTransaction(intent, context)
    }
}