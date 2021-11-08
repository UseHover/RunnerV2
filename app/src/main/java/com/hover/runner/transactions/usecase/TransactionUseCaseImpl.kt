package com.hover.runner.transactions.usecase

import androidx.lifecycle.LiveData
import com.hover.runner.transactions.RunnerTransaction

class TransactionUseCaseImpl(private val transactionRepoInterface: TransactionRepoInterface) : TransactionUseCase {
    override fun getAllTransactions(): LiveData<List<RunnerTransaction>> {
        return transactionRepoInterface.getAllTransactions()
    }

    override fun getTransaction(uuid: String): LiveData<RunnerTransaction> {
        return transactionRepoInterface.getTransaction(uuid)
    }

    override fun getTransactionsByAction(actionId: String): LiveData<List<RunnerTransaction>> {
        return transactionRepoInterface.getTransactionsByAction(actionId)
    }
}