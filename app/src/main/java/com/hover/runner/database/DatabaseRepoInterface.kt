package com.hover.runner.database

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.hover.runner.transactions.RunnerTransaction
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.parsers.HoverParser

interface DatabaseRepoInterface {
    suspend fun getAllActionsFromHover() : List<HoverAction>
    suspend fun getHoverAction(id: String) : HoverAction

    suspend fun getTransactionSuspended(uuid: String) : RunnerTransaction?
    suspend fun getLastTransaction(actionId: String) : RunnerTransaction?
    suspend fun getTransactionsByActionSuspended(actionId: String) : List<RunnerTransaction>
    fun getAllTransactions() : LiveData<List<RunnerTransaction>>
    fun getTransaction(uuid: String) : LiveData<RunnerTransaction>
    fun getTransactionsByAction(actionId: String) : LiveData<List<RunnerTransaction>>
    fun getTransactionsByAction(actionId: String, limit: Int) : LiveData<List<RunnerTransaction>>
    fun updateTransaction(transaction: RunnerTransaction)
    fun insertTransaction(transaction: RunnerTransaction)
    fun insertOrUpdateTransaction(intent: Intent, context: Context)

    suspend fun getParsersByActionId(actionId: String?): List<HoverParser>
    suspend fun getParser(id: Int): HoverParser?
}