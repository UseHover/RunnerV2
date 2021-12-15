package com.hover.runner.filter.filter_actions.repo

import android.content.Context
import com.hover.runner.action.models.Action
import com.hover.runner.action.repo.ActionRepo
import com.hover.runner.filter.filter_actions.model.ActionFilterParameters
import com.hover.runner.parser.repo.ParserRepo
import com.hover.runner.sim.repo.SimRepo
import com.hover.runner.transaction.repo.TransactionRepo

class ActionFilterRepoImpl(private val actionRepo: ActionRepo,
                           private val transactionRepo: TransactionRepo,
                           private val parserRepo: ParserRepo,
                           private val simRepo: SimRepo,
                           private val context: Context) : ActionFilterRepoInterface {

	override suspend fun filter(actionFilterParameters: ActionFilterParameters): List<Action> {
		with(actionFilterParameters) {
			val allActionIds = actionRepo.getAllHoverActionIds()

			val step1 = filterThroughActions(allActionIds, this)
			val step2 = filterThroughTransactions(step1, this)
			val step3 = filterThroughParsers(step2, this)
			val step4 = filterThroughPresentSim(step3, this)

			val toHoverActions = actionRepo.getHoverActions(step4)
			return Action.getList(toHoverActions, transactionRepo, context)
		}
	}
	private suspend fun filterThroughActions(allIds: Array<String>,params: ActionFilterParameters) : Array<String> {
		var filteredList  = allIds

		if(params.actionId.isNotEmpty()) filteredList = actionRepo.filterByActionId(filteredList, params.actionId)
		if(params.actionRootCode.isNotEmpty()) filteredList = actionRepo.filterByRootCode(filteredList, params.actionRootCode)
		if(params.countryCodeList.isNotEmpty()) filteredList = actionRepo.filterByCountries(filteredList, params.countryCodeList.toTypedArray())

		params.getTotalActionIds(context).apply { //Network names parameter is accounted for in total ids
			if(this.isNotEmpty()) {
				filteredList = if(filteredList.isEmpty())  actionRepo.filterByActionIds(this)
				else actionRepo.filterByActionIds(filteredList, this)
			}
		}

		return filteredList
	}

	private suspend fun filterThroughTransactions(selectedActionIds: Array<String>, params: ActionFilterParameters) : Array<String>{
		var tempList = selectedActionIds

		if(params.categoryList.isNotEmpty()) tempList = transactionRepo.getActionIdsByCategories(tempList, params.categoryList.toTypedArray())
		if(params.endDate > 0) tempList = transactionRepo.getActionIdsByDateRange(tempList, params.startDate, params.endDate)

		if(params.shouldFilterByTransactionStatus() || params.includeActionsWithNoTransaction) {
			val idsWithTransaction : Array<String> = getActionIdsInTransactions(selectedActionIds, params)

			if (params.shouldFilterByTransactionStatus()) {
				if(idsWithTransaction.isEmpty())tempList = emptyArray()
				else tempList += idsWithTransaction
			}

			if (params.includeActionsWithNoTransaction) {
				tempList += getActionIdsWithNoTransaction(selectedActionIds, idsWithTransaction)
			}
		}
		return tempList.distinct().toTypedArray()
	}

	private suspend fun filterThroughParsers(selectedActionIds: Array<String>, params: ActionFilterParameters) : Array<String> {
		return if(params.hasParser) parserRepo.filterActionIds(selectedActionIds)
		else selectedActionIds
	}
	private suspend fun filterThroughPresentSim(selectedList: Array<String>, param: ActionFilterParameters) : Array<String> {
		return if(param.onlyWithSimPresent) {
			val presentSimCountryCodes = simRepo.getPresentSims().map { it.countryIso }
			return actionRepo.filterByCountries(selectedList, presentSimCountryCodes.toTypedArray())
		}
		else selectedList
	}

	private fun getActionIdsWithNoTransaction(allActionIds: Array<String>, withTransactionList: Array<String>) : Array<String> {
		return (allActionIds.toList() - withTransactionList.toList().toSet()).toTypedArray()
	}
	private suspend fun getActionIdsInTransactions(selectedActionIds: Array<String>, params: ActionFilterParameters) : Array<String> {
		var subList = arrayOf("")
		if(params.isTransactionSuccessfulIncluded()) subList += transactionRepo.getActionIdsByTransactionSuccessful(selectedActionIds)
		if(params.isTransactionPendingIncluded()) subList += transactionRepo.getActionIdsByTransactionPending(selectedActionIds)
		if(params.isTransactionFailedIncluded()) subList += transactionRepo.getActionIdsByTransactionFailed(selectedActionIds)

		return if(subList.size == 1) emptyArray() //arrayOf("") returns the first empty space as a value
		else subList.distinct().toTypedArray()
	}
}