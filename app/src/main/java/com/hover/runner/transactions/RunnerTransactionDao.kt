package com.hover.runner.transactions

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface RunnerTransactionDao {
    @Query("SELECT * FROM runner_transactions WHERE action_id = :actionId ORDER BY initiated_at DESC")
    fun transactionsByAction(actionId: String): LiveData<List<RunnerTransaction>>

    @Query("SELECT * FROM runner_transactions WHERE action_id = :actionId ORDER BY initiated_at DESC")
    suspend fun transactionsByAction_Suspended(actionId: String): List<RunnerTransaction>

    @Query("SELECT * FROM runner_transactions WHERE action_id = :actionId ORDER BY initiated_at DESC LIMIT :limit")
    fun transactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>>

    @Query("SELECT * FROM runner_transactions WHERE action_id = :actionId ORDER BY initiated_at ASC LIMIT 1")
    suspend fun lastTransactionsByAction_Suspended(actionId: String): RunnerTransaction?

    @Query("SELECT * FROM runner_transactions ORDER BY initiated_at DESC")
    suspend  fun allTransactions(): List<RunnerTransaction>

    @Query("SELECT * FROM runner_transactions WHERE uuid = :uuid LIMIT 1")
    fun getTransaction(uuid: String): LiveData<RunnerTransaction>

    @Query("SELECT * FROM runner_transactions WHERE uuid = :uuid LIMIT 1")
    suspend fun getTransaction_Suspended(uuid: String): RunnerTransaction?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(transaction: RunnerTransaction?)

    @Update
    fun update(transaction: RunnerTransaction?)
}