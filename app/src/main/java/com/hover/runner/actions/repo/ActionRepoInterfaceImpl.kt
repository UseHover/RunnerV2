package com.hover.runner.actions.repo

import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.models.Action
import com.hover.runner.actions.models.StreamlinedSteps
import com.hover.runner.parser.model.Parser
import com.hover.runner.parser.repo.ParserRepo
import com.hover.runner.transactions.repo.TransactionRepo

class ActionRepoInterfaceImpl(private val actionRepo: ActionRepo,
                              private val transactionRepo: TransactionRepo,
                              private val parserRepo: ParserRepo) : ActionRepoInterface {

    override suspend fun getAllActions(): List<Action> {
        val hoverActions =  actionRepo.getAllActionsFromHover()

        val runnerActions = mutableListOf<Action>()
        hoverActions.forEachIndexed { pos, act ->
            val lastTransaction = transactionRepo.getLastTransaction(act.public_id)
            runnerActions[pos] = Action.get(act, lastTransaction)
        }
        return runnerActions;
    }

    override suspend fun getActionDetailsById(id: String) : ActionDetails {
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
        return Action.get(act, lastTransaction)
    }
}