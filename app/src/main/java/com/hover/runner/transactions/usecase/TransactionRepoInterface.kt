package com.hover.runner.transactions.usecase

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.hover.runner.transactions.RunnerTransaction

interface TransactionRepoInterface {
    fun getAllTransactions() : LiveData<List<RunnerTransaction>>
    fun getTransaction(uuid: String) : LiveData<RunnerTransaction>
    suspend fun getTransactionSuspended(uuid: String) : RunnerTransaction?
    fun getTransactionsByAction(actionId: String) : LiveData<List<RunnerTransaction>>
    suspend fun getLastTransaction(actionId: String) : RunnerTransaction?

    fun updateTransaction(transaction: RunnerTransaction)
    fun insertTransaction(transaction: RunnerTransaction)
    fun insertOrUpdateTransaction(intent: Intent, context: Context)
}