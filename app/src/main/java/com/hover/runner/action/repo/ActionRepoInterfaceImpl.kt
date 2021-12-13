package com.hover.runner.action.repo

import android.content.Context
import com.hover.runner.action.models.Action
import com.hover.runner.action.models.ActionDetails
import com.hover.runner.action.models.StreamlinedSteps
import com.hover.runner.filter.filter_actions.model.ActionFilterParameters
import com.hover.runner.parser.model.Parser
import com.hover.runner.parser.repo.ParserRepo
import com.hover.runner.sim.repo.SimRepo
import com.hover.runner.transaction.repo.TransactionRepo
import com.hover.sdk.actions.HoverAction

class ActionRepoInterfaceImpl(private val actionRepo: ActionRepo,
                              private val transactionRepo: TransactionRepo,
                              private val parserRepo: ParserRepo,
                              private val context: Context) : ActionRepoInterface {

	override suspend fun getAllActions(): List<Action> {
		val hoverActions = actionRepo.getAllHoverActions()
		return Action.getList(hoverActions, transactionRepo, context)
	}

	override suspend fun getActionDetailsById(id: String): ActionDetails {
		val transactionList = transactionRepo.getTransactionsByAction(id)
		val parsersList = parserRepo.getParsersByActionId(id)
		val hoverAction = actionRepo.getHoverAction(id)

		val parserString = Parser.listIdsToString(parsersList)
		val streamlinedSteps = StreamlinedSteps.get(hoverAction.root_code, hoverAction.custom_steps)

		val actionDetails = ActionDetails.init(transactionList)
		actionDetails.streamlinedSteps = streamlinedSteps
		actionDetails.parsers = parserString
		actionDetails.operators = hoverAction.network_name
		return actionDetails
	}

	override suspend fun getAction(id: String): Action {
		val act = actionRepo.getHoverAction(id)
		val lastTransaction = transactionRepo.getLastTransaction(act.public_id)
		return Action.getList(act, lastTransaction, context)
	}

	override suspend fun getAllActionCountryCodes(): List<String> {
		return actionRepo.getAllActionsCountryCodes()
	}

	override suspend fun getNetworkNames(countryCodes: List<String>): List<String> {
		return explodeNetworkNameList(actionRepo.getNetworkNamesByCountryCodes(countryCodes))
	}

	override suspend fun getAllNetworkNames(): List<String> {
		return explodeNetworkNameList(actionRepo.getAllNetworkNames())
	}

	private fun explodeNetworkNameList(rawNetworkNames : List<String>) : List<String>{
		val explodedNetworkNames = mutableListOf<String>()
		rawNetworkNames.forEach {
			val joinedNetworkNames = it.split(",")
			joinedNetworkNames.forEach { each-> explodedNetworkNames.add(each) }
		}
		return explodedNetworkNames
	}


	override fun getContext(): Context {
		return context
	}
}