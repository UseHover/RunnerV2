package com.hover.runner.actions.repo

import android.content.Context
import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.models.Action
import com.hover.runner.actions.models.StreamlinedSteps
import com.hover.runner.parser.model.Parser
import com.hover.runner.parser.repo.ParserRepo
import com.hover.runner.transactions.repo.TransactionRepo
import timber.log.Timber

class ActionRepoInterfaceImpl(private val actionRepo: ActionRepo,
                              private val transactionRepo: TransactionRepo,
                              private val parserRepo: ParserRepo,
                              private val context: Context) : ActionRepoInterface {

    override suspend fun getAllActions(): List<Action> {
        val hoverActions =  actionRepo.getAllActionsFromHover()

        val runnerActions = mutableListOf<Action>()
            hoverActions.forEachIndexed { _, act ->
                val lastTransaction = transactionRepo.getLastTransaction(act.id)
                runnerActions.add(Action.get(act, lastTransaction, context))
            }
        return runnerActions;
    }

    override suspend fun getActionDetailsById(id: String) : ActionDetails {
        val transactionList = transactionRepo.getTransactionsByAction(id)
        val parsersList = parserRepo.getParsersByActionId(id)
        val hoverAction = actionRepo.getHoverAction(id)

        val parserString = Parser.listIdsToString(parsersList)
        val streamlinedSteps = StreamlinedSteps.get(hoverAction.rootCode, hoverAction.steps)

        val actionDetails = ActionDetails.init(transactionList)
        actionDetails.streamlinedSteps = streamlinedSteps
        actionDetails.parsers = parserString
        actionDetails.operators = hoverAction.networkName
        return actionDetails
    }

    override suspend fun getAction(id: String): Action {
        val act = actionRepo.getHoverAction(id)
        val lastTransaction = transactionRepo.getLastTransaction(act.id)
        return Action.get(act, lastTransaction, context)
    }

    override fun getContext(): Context {
        return context
    }
}