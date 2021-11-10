package com.hover.runner.transactions.repo

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.hover.runner.transactions.RunnerTransaction

interface TransactionRepoInterface {
    fun getTransaction(uuid: String) : LiveData<RunnerTransaction>
    fun getTransactionsByAction(actionId: String, limit: Int) : LiveData<List<RunnerTransaction>>
    suspend fun getAllTransactions() : List<RunnerTransaction>
    suspend fun getTransactionsByAction(actionId: String) : List<RunnerTransaction>
    suspend fun getTransactionSuspended(uuid: String) : RunnerTransaction?
    suspend fun getLastTransaction(actionId: String) : RunnerTransaction?
}