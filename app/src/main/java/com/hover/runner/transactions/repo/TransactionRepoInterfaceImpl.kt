package com.hover.runner.transactions.repo

import androidx.lifecycle.LiveData
import com.hover.runner.transactions.RunnerTransaction

class TransactionRepoInterfaceImpl(private val repo: TransactionRepo) : TransactionRepoInterface {
    override suspend fun getAllTransactions(): List<RunnerTransaction> {
        return repo.getAllTransactions()
    }
    override suspend fun getTransactionsByAction(actionId: String): List<RunnerTransaction> {
        return repo.getTransactionsByAction(actionId)
    }

    override fun getTransaction(uuid: String): LiveData<RunnerTransaction> {
        return repo.getTransaction(uuid)
    }

    override suspend fun getTransactionSuspended(uuid: String): RunnerTransaction? {
        return repo.getTransactionSuspended(uuid)
    }

    override fun getTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>> {
        return repo.getTransactionsByAction(actionId, limit)
    }

    override suspend fun getLastTransaction(actionId: String): RunnerTransaction? {
        return repo.getLastTransaction(actionId)
    }
}