package com.hover.runner.transactions.repo

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.hover.runner.transactions.RunnerTransaction

interface TransactionRepoInterface {
    fun getAllTransactions() : LiveData<List<RunnerTransaction>>
    fun getTransaction(uuid: String) : LiveData<RunnerTransaction>
    suspend fun getTransactionSuspended(uuid: String) : RunnerTransaction?
    fun getTransactionsByAction(actionId: String) : LiveData<List<RunnerTransaction>>
    fun getTransactionsByAction(actionId: String, limit: Int) : LiveData<List<RunnerTransaction>>
    suspend fun getLastTransaction(actionId: String) : RunnerTransaction?
}