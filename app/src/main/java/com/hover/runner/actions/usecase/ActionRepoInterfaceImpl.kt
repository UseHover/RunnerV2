package com.hover.runner.actions.usecase

import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.models.Action
import com.hover.runner.database.DatabaseRepo

class ActionRepoInterfaceImpl(private val databaseRepo: DatabaseRepo) : ActionRepoInterface {
    override suspend fun getAllActionsFromHover(): List<Action> {
        return databaseRepo.getAllActionsFromHover()
    }

    override suspend fun getActionDetailsById(id: String) : ActionDetails {
        return databaseRepo.getActionDetailsById(id)
    }

    override suspend fun getAction(id: String): Action {
        TODO("Not yet implemented")
    }
}