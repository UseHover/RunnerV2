package com.hover.runner.filter.filter_transactions.repo

import android.content.Context
import com.hover.runner.action.repo.ActionIdsInANetworkRepo
import com.hover.runner.action.repo.ActionRepo
import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.repo.TransactionRepo
import timber.log.Timber

class TransactionFilterRepoImpl(private val actionRepo: ActionRepo,
                                private val transactionRepo: TransactionRepo,
                                private val context: Context) : TransactionFilterRepoInterface {

	override suspend fun filter(params: TransactionFilterParameters): List<RunnerTransaction> {
		val step1_action_ids = filterThroughActions(params)
		val step2_uuids = filterThroughTransactions(step1_action_ids, params)

		return transactionRepo.getTransactions(step2_uuids)
	}

	private suspend fun filterThroughActions(params: TransactionFilterParameters) : Array<String> {
		var filteredActionIds  = emptyArray<String>()

		if(params.countryCodeList.isNotEmpty()) filteredActionIds =  actionRepo.filterByCountries(params.countryCodeList.toTypedArray())

		params.getTotalActionIds(context).apply { // Network names parameter is accounted for in total ids
			if(this.isNotEmpty()) filteredActionIds = filterFromActionIds(filteredActionIds, this)
		}

		return filteredActionIds
	}

	private suspend fun filterThroughTransactions(selectedActionIds: Array<String>, params: TransactionFilterParameters) : Array<String>{
		var selectedUUIDs = transactionRepo.getUUIDsByActionIds(selectedActionIds)

		if(params.endDate > 0) selectedUUIDs =  getUUIDsByDateRange(selectedUUIDs, params.startDate, params.endDate)

		if (params.shouldFilterByTransactionStatus()) {
			val filteredUUIDs : Array<String> = getUUIDInTransactions(selectedUUIDs, params)
			if(filteredUUIDs.isEmpty()) selectedUUIDs = emptyArray()
			else selectedUUIDs += filteredUUIDs
		}
		return selectedUUIDs.distinct().toTypedArray()
	}


	private suspend fun getUUIDInTransactions(selectedUUIDs: Array<String>, params: TransactionFilterParameters) : Array<String> {
		if(params.isActionRelatedParamSelected() && selectedUUIDs.isEmpty()) return emptyArray()
		else{
			var subList = arrayOf("")
			if(params.isTransactionSuccessfulIncluded()) subList += getSuccessfulUUIDs(selectedUUIDs)
			if(params.isTransactionPendingIncluded()) subList += getPendingUUIDs(selectedUUIDs)
			if(params.isTransactionFailedIncluded()) subList += getFailedUUIDs(selectedUUIDs)

			return if(subList.size == 1) emptyArray() //arrayOf("") returns the first empty space as a value
				   else subList.distinct().toTypedArray()
		}
	}

	private suspend fun filterFromActionIds(filteredActionIds: Array<String>, actionIds: Array<String>) : Array<String> {
		return if(filteredActionIds.isEmpty())  actionRepo.filterByActionIds(actionIds)
		else actionRepo.filterByActionIds(filteredActionIds, actionIds)
	}
	private suspend fun getUUIDsByDateRange(selectedUUIDs: Array<String>,  startDate : Long, endDate: Long) : Array<String> {
		return if(selectedUUIDs.isEmpty())  transactionRepo.getUUIDsByDateRange(startDate, endDate)
		else transactionRepo.getUUIDsByDateRange(selectedUUIDs, startDate, endDate)
	}
	private suspend fun getSuccessfulUUIDs(selectedUUIDs: Array<String>) : Array<String> {
		return if(selectedUUIDs.isEmpty()) transactionRepo.getSuccessfulUUIDs()
		else transactionRepo.getSuccessfulUUIDs(selectedUUIDs)
	}
	private suspend fun getPendingUUIDs(selectedUUIDs: Array<String>) : Array<String> {
		return if(selectedUUIDs.isEmpty())  transactionRepo.getPendingUUIDs()
		else transactionRepo.getPendingUUIDs(selectedUUIDs)
	}
	private suspend fun getFailedUUIDs(selectedUUIDs: Array<String>) : Array<String> {
		return if(selectedUUIDs.isEmpty())  transactionRepo.getFailedUUIDs()
		else transactionRepo.getFailedUUIDs(selectedUUIDs)
	}
}