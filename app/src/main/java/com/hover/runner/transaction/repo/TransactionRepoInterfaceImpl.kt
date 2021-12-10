package com.hover.runner.transaction.repo

import android.content.Context
import androidx.lifecycle.LiveData
import com.hover.runner.action.models.Action
import com.hover.runner.action.repo.ActionRepo
import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.utils.Utils
import com.hover.sdk.api.Hover
import com.hover.sdk.sms.MessageLog
import com.hover.sdk.transactions.Transaction

class TransactionRepoInterfaceImpl(private val repo: TransactionRepo,
                                   private val actionRepo: ActionRepo,
                                   private val context: Context) : TransactionRepoInterface {
	override suspend fun getAllTransactions(): List<RunnerTransaction> {
		return repo.getAllTransactions()
	}

	override suspend fun getTransactionsByAction(actionId: String): List<RunnerTransaction> {
		return repo.getTransactionsByAction(actionId)
	}

	override suspend fun filter(transactionFilterParameters: TransactionFilterParameters): List<RunnerTransaction> {
		return repo.filterTransactions(transactionFilterParameters)
	}

	override suspend fun getAllCategories(): List<String> {
		return repo.getCategories()
	}

	override fun getTransaction(uuid: String): LiveData<RunnerTransaction> {
		return repo.getTransaction(uuid)
	}

	override suspend fun getTransactionSuspended(uuid: String): RunnerTransaction? {
		return repo.getTransactionSuspended(uuid)
	}

	override fun getTransactionsByAction(actionId: String,
	                                     limit: Int): LiveData<List<RunnerTransaction>> {
		return repo.getTransactionsByAction(actionId, limit)
	}

	override suspend fun getLastTransaction(actionId: String): RunnerTransaction? {
		return repo.getLastTransaction(actionId)
	}

	override suspend fun getAction(actionId: String): Action {
		val hoverAction = actionRepo.getHoverAction(actionId)
		val lastTransaction = repo.getLastTransaction(actionId)
		return Action.get(hoverAction, lastTransaction, context)
	}

	override suspend fun getDeviceId(): String {
		return Utils.getDeviceId(context) ?: ""
	}

	override suspend fun getHoverTransaction(uuid: String): Transaction {
		return Hover.getTransaction(uuid, context)
	}

	override suspend fun getMessageLog(smsUUID: String): MessageLog {
		return Hover.getSMSMessageByUUID(smsUUID, context)
	}


}