package com.hover.runner.transactions.usecase

import androidx.lifecycle.LiveData
import com.hover.runner.transactions.RunnerTransaction

interface TransactionUseCase {
    fun getAllTransactions() : LiveData<List<RunnerTransaction>>
    fun getTransaction(uuid: String) : LiveData<RunnerTransaction>
    fun getTransactionsByAction(actionId: String)  : LiveData<List<RunnerTransaction>>
}