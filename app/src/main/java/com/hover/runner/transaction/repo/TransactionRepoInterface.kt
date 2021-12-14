package com.hover.runner.transaction.repo

import androidx.lifecycle.LiveData
import com.hover.runner.actions.StyledAction
import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.sdk.sms.MessageLog
import com.hover.sdk.transactions.Transaction

interface TransactionRepoInterface {
	fun getTransaction(uuid: String): LiveData<RunnerTransaction>
	fun getTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>>

	suspend fun filter(transactionFilterParameters: TransactionFilterParameters): List<RunnerTransaction>
	suspend fun getAllCategories(): List<String>
	suspend fun getAllTransactions(): List<RunnerTransaction>
	suspend fun getTransactionsByAction(actionId: String): List<RunnerTransaction>
	suspend fun getTransactionSuspended(uuid: String): RunnerTransaction?
	suspend fun getLastTransaction(actionId: String): RunnerTransaction?
	suspend fun getAction(actionId: String): StyledAction
	suspend fun getDeviceId(): String
	suspend fun getHoverTransaction(uuid: String): Transaction
	suspend fun getMessageLog(smsUUID: String): MessageLog
}