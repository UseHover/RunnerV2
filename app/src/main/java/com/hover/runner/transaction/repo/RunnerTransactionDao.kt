package com.hover.runner.transaction.repo

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hover.runner.transaction.model.RunnerTransaction


@Dao
interface RunnerTransactionDao {
	@Query("SELECT * FROM runner_transactions WHERE action_id = :actionId ORDER BY initiated_at DESC")
	fun transactionsByAction(actionId: String): LiveData<List<RunnerTransaction>>

	@Query("SELECT * FROM runner_transactions WHERE action_id = :actionId ORDER BY initiated_at DESC")
	suspend fun transactionsByAction_Suspended(actionId: String): List<RunnerTransaction>

	@Query("SELECT * FROM runner_transactions WHERE action_id = :actionId ORDER BY initiated_at DESC LIMIT :limit")
	fun transactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>>

	@Query("SELECT * FROM runner_transactions WHERE matched_parsers LIKE :parserId ORDER BY initiated_at DESC ")
	suspend fun transactionsByParser(parserId: String): List<RunnerTransaction>

	@Query("SELECT * FROM runner_transactions WHERE action_id = :actionId ORDER BY initiated_at ASC LIMIT 1")
	suspend fun lastTransactionsByAction_Suspended(actionId: String): RunnerTransaction?

	@Query("SELECT * FROM runner_transactions ORDER BY initiated_at DESC")
	suspend fun allTransactions(): List<RunnerTransaction>

	@Query("SELECT category FROM runner_transactions ORDER BY initiated_at DESC")
	suspend fun allCategories(): List<String>

	@Query("SELECT * FROM runner_transactions WHERE uuid = :uuid LIMIT 1")
	fun getTransaction(uuid: String): LiveData<RunnerTransaction>

	@Query("SELECT * FROM runner_transactions WHERE uuid = :uuid LIMIT 1")
	suspend fun getTransaction_Suspended(uuid: String?): RunnerTransaction?

	@Query("SELECT DISTINCT action_id FROM runner_transactions WHERE action_id IN (:actionIdSubList) AND updated_at >= :startDate AND updated_at <= :endDate ")
	suspend fun getActionIdsByDateRange(actionIdSubList:Array<String>, startDate : Long, endDate: Long): Array<String>

	@Query("SELECT DISTINCT action_id FROM runner_transactions WHERE action_id IN (:actionIdSubList) AND category IN (:categories) ")
	suspend fun getActionIdsByCategories(actionIdSubList:Array<String>, categories: Array<String>): Array<String>

	@Query("SELECT DISTINCT action_id FROM runner_transactions WHERE action_id IN (:actionIdSubList) AND status = 'succeeded' ")
	suspend fun getActionIdsByTransactionSuccessful(actionIdSubList:Array<String>): Array<String>

	@Query("SELECT DISTINCT action_id FROM runner_transactions WHERE action_id IN (:actionIdSubList) AND status = 'pending' ")
	suspend fun getActionIdsByTransactionPending(actionIdSubList:Array<String>): Array<String>

	@Query("SELECT DISTINCT action_id FROM runner_transactions WHERE action_id IN (:actionIdSubList) AND status = 'failed' ")
	suspend fun getActionIdsByTransactionFailed(actionIdSubList:Array<String>): Array<String>

	@Query("SELECT DISTINCT action_id FROM runner_transactions")
	suspend fun getActionIdsWithTransactions(): List<String>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun insert(transaction: RunnerTransaction?)

	@Update
	fun update(transaction: RunnerTransaction?)
}