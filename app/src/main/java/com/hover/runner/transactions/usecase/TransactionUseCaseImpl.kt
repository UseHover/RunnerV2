package com.hover.runner.transactions.usecase

import androidx.lifecycle.LiveData
import com.hover.runner.transactions.RunnerTransaction
import com.hover.runner.transactions.repo.TransactionRepoInterface

class TransactionUseCaseImpl(private val transactionRepoInterface: TransactionRepoInterface) : TransactionUseCase {
    override suspend fun getAllTransactions(): List<RunnerTransaction> {
        return transactionRepoInterface.getAllTransactions()
    }

    override suspend fun getTransactionsByAction(actionId: String): List<RunnerTransaction> {
        return transactionRepoInterface.getTransactionsByAction(actionId)
    }

    override fun getTransaction(uuid: String): LiveData<RunnerTransaction> {
        return transactionRepoInterface.getTransaction(uuid)
    }

    override fun getTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>> {
        return transactionRepoInterface.getTransactionsByAction(actionId, limit)
    }
}