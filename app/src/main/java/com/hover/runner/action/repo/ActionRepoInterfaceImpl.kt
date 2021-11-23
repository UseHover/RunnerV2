package com.hover.runner.action.repo

import android.content.Context
import com.hover.runner.action.models.Action
import com.hover.runner.action.models.ActionDetails
import com.hover.runner.action.models.StreamlinedSteps
import com.hover.runner.parser.model.Parser
import com.hover.runner.parser.repo.ParserRepo
import com.hover.runner.transaction.repo.TransactionRepo
import timber.log.Timber

class ActionRepoInterfaceImpl(
    private val actionRepo: ActionRepo,
    private val transactionRepo: TransactionRepo,
    private val parserRepo: ParserRepo,
    private val context: Context
) : ActionRepoInterface {

    override suspend fun getAllActions(): List<Action> {
        val hoverActions = actionRepo.getAllActionsFromHover()

        val runnerActions = mutableListOf<Action>()
        hoverActions.forEachIndexed { _, act ->
            val lastTransaction = transactionRepo.getLastTransaction(act.public_id)
            Timber.i("Action d ${act.public_id}")
            Timber.i("Action t ${act.network_name}")
            runnerActions.add(Action.get(act, lastTransaction, context))
        }
        return runnerActions
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
        return Action.get(act, lastTransaction, context)
    }

    override fun getContext(): Context {
        return context
    }
}