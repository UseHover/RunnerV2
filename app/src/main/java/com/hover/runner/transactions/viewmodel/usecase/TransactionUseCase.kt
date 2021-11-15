package com.hover.runner.transactions.viewmodel.usecase

import androidx.lifecycle.LiveData
import com.hover.runner.transactions.model.RunnerTransaction

interface TransactionUseCase {
    suspend fun getAllTransactions() : List<RunnerTransaction>
    suspend fun getTransactionsByAction(actionId: String)  : List<RunnerTransaction>
    fun getTransaction(uuid: String) : LiveData<RunnerTransaction>
    fun getTransactionsByAction(actionId: String, limit: Int)  : LiveData<List<RunnerTransaction>>
}