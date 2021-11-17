package com.hover.runner.transactions.viewmodel.usecase

import androidx.lifecycle.LiveData
import com.hover.runner.transactions.model.RunnerTransaction
import com.hover.runner.transactions.model.TransactionDetailsInfo
import com.hover.runner.transactions.model.TransactionDetailsMessages

interface TransactionUseCase {
    suspend fun getAllTransactions(): List<RunnerTransaction>
    suspend fun getTransactionsByAction(actionId: String): List<RunnerTransaction>
    fun getTransaction(uuid: String): LiveData<RunnerTransaction>
    fun getTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>>

    suspend fun getAboutInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsInfo>
    suspend fun getDeviceInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsInfo>
    suspend fun getDebugInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsInfo>
    suspend fun getMessagesInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsMessages>
}