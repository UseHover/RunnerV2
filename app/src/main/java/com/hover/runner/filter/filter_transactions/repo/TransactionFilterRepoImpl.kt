package com.hover.runner.filter.filter_transactions.repo

import android.content.Context
import com.hover.runner.action.repo.ActionIdsInANetworkRepo
import com.hover.runner.action.repo.ActionRepo
import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.repo.TransactionRepo

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
		if(params.networkNameList.isNotEmpty()) params.actionIdList += ActionIdsInANetworkRepo.getIds(params.networkNameList, context)

		if(params.actionIdList.isNotEmpty()){
			filteredActionIds = if(filteredActionIds.isEmpty())  actionRepo.filterByActionIds(params.actionIdList.toTypedArray())
			else actionRepo.filterByActionIds(filteredActionIds, params.actionIdList.toTypedArray())
		}
		return filteredActionIds
	}

	private suspend fun filterThroughTransactions(selectedActionIds: Array<String>, params: TransactionFilterParameters) : Array<String>{
		var selectedUUIDs = transactionRepo.getUUIDsByActionIds(selectedActionIds)

		if(params.endDate > 0) {
			if(selectedUUIDs.isEmpty()) selectedUUIDs =  transactionRepo.getUUIDsByDateRange(params.startDate, params.endDate)
			selectedUUIDs = transactionRepo.getUUIDsByDateRange(selectedUUIDs, params.startDate, params.endDate)
		}

		if (params.shouldFilterByTransactionStatus()) {
			val filteredUUIDs : Array<String> = getUUIDInTransactions(selectedUUIDs, params)
			if(filteredUUIDs.isEmpty()) selectedUUIDs = emptyArray()
			else selectedUUIDs += filteredUUIDs
		}

		return selectedUUIDs.distinct().toTypedArray()
	}


	private suspend fun getUUIDInTransactions(selectedUUIDs: Array<String>, params: TransactionFilterParameters) : Array<String> {
		/*Two options to handle this is to ensure "selectedUUIDs is prefilled with ids, but that..
		...will cost a running operation of gettingAllUUIDs() and extra memory cost. See ActionFilterRepoImpl.kt, step 1 for more context

		..The other option is the chosen method, where we do if and else for the state of UUIDs.
		..the cost is code readability. When it gets complicated to read, we should change to option 1 or a better way.
		 */
		var subList = arrayOf("")
		if(params.isTransactionSuccessfulIncluded()) {
			subList += if(selectedUUIDs.isEmpty()) transactionRepo.getSuccessfulUUIDs()
			else transactionRepo.getSuccessfulUUIDs(selectedUUIDs)
		}
		if(params.isTransactionPendingIncluded()) {
			subList += if(selectedUUIDs.isEmpty()) transactionRepo.getPendingUUIDs()
			else transactionRepo.getPendingUUIDs(selectedUUIDs)
		}
		if(params.isTransactionFailedIncluded()) {
			subList += if(selectedUUIDs.isEmpty()) transactionRepo.getFailedUUIDs()
			else transactionRepo.getFailedUUIDs(selectedUUIDs)
		}
		return if(subList[0] =="") emptyArray() else subList.distinct().toTypedArray() //arrayOf("") returns empty space as a value
	}
}