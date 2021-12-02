package com.hover.runner.transaction.viewmodel.usecase

import androidx.lifecycle.LiveData
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.model.TransactionDetailsInfo
import com.hover.runner.transaction.model.TransactionDetailsMessages

interface TransactionUseCase {
    suspend fun getAllTransactions(): List<RunnerTransaction>
    suspend fun getTransactionsByAction(actionId: String): List<RunnerTransaction>
    fun getTransaction(uuid: String): LiveData<RunnerTransaction>
    fun getTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>>
    suspend fun getDistinctTransactionCategories() : List<String>

    suspend fun getAboutInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsInfo>
    suspend fun getDeviceInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsInfo>
    suspend fun getDebugInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsInfo>
    suspend fun getMessagesInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsMessages>
}