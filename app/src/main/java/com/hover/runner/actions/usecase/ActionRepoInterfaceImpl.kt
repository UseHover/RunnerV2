package com.hover.runner.actions.usecase

import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.models.Action
import com.hover.runner.database.DatabaseRepo

class ActionRepoInterfaceImpl(private val databaseRepo: DatabaseRepo) : ActionRepoInterface {
    override suspend fun getAllActions(): List<Action> {
        val hoverActions =  databaseRepo.getAllActionsFromHover()

        val runnerActions = mutableListOf<Action>()
        hoverActions.forEachIndexed { pos, act ->
            val lastTransaction = databaseRepo.getLastTransaction(act.public_id)
            runnerActions[pos] = Action.get(act, lastTransaction)
        }
        return runnerActions;

    }

    override suspend fun getActionDetailsById(id: String) : ActionDetails {
        TODO()
    }

    override suspend fun getAction(id: String): Action {
        val act = databaseRepo.getHoverAction(id)
        val lastTransaction = databaseRepo.getLastTransaction(act.public_id)
        return Action.get(act, lastTransaction)
    }
}