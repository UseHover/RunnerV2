package com.hover.runner.filter.filter_transactions.abstractDao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AbstractTransactionFilterDao {
	//For Action ID Filtering
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


	//For Transaction UUID Filtering
	@Query("SELECT DISTINCT uuid FROM runner_transactions WHERE action_id IN (:actionIds)")
	suspend fun getUUIDsByActionIds(actionIds: Array<String>): Array<String>

	@Query("SELECT DISTINCT uuid FROM runner_transactions WHERE uuid IN (:selectedUUIDs) AND updated_at >= :startDate AND updated_at <= :endDate ")
	suspend fun getTransactionsByDateRange(selectedUUIDs: Array<String>, startDate : Long, endDate: Long): Array<String>
	@Query("SELECT DISTINCT uuid FROM runner_transactions WHERE updated_at >= :startDate AND updated_at <= :endDate ")
	suspend fun getTransactionsByDateRange(startDate : Long, endDate: Long): Array<String>

	@Query("SELECT DISTINCT uuid FROM runner_transactions WHERE uuid IN (:selectedUUIDs) AND status =  'succeeded' ")
	suspend fun getSuccessfulTransactions(selectedUUIDs: Array<String>): Array<String>
	@Query("SELECT DISTINCT uuid FROM runner_transactions WHERE status =  'succeeded' ")
	suspend fun getSuccessfulTransactions(): Array<String>


	@Query("SELECT DISTINCT uuid FROM runner_transactions WHERE uuid IN (:selectedUUIDs) AND status =  'pending' ")
	suspend fun getPendingTransactions(selectedUUIDs: Array<String>): Array<String>
	@Query("SELECT DISTINCT uuid FROM runner_transactions WHERE status =  'pending' ")
	suspend fun getPendingTransactions(): Array<String>

	@Query("SELECT DISTINCT uuid FROM runner_transactions WHERE uuid IN (:selectedUUIDs) AND status =  'failed' ")
	suspend fun getFailedTransactions(selectedUUIDs: Array<String>): Array<String>
	@Query("SELECT DISTINCT uuid FROM runner_transactions WHERE status =  'failed' ")
	suspend fun getFailedTransactions(): Array<String>

}