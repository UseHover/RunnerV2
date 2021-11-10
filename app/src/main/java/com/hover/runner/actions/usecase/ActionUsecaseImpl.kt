package com.hover.runner.actions.usecase

import com.hover.runner.actions.models.Action
import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.repo.ActionRepoInterface

class ActionUseCaseImpl(private val actionRepo: ActionRepoInterface)  : ActionUseCase {
    override suspend fun loadAll(): List<Action> {
        return actionRepo.getAllActionsFromHover()
    }

    override suspend fun filter(): List<Action> {
        TODO("Not yet implemented")
    }

    override suspend fun getAction(id: String): Action {
        TODO("Not yet implemented")
    }

    override suspend fun getActionDetails(id: String): ActionDetails {
        return actionRepo.getActionDetailsById(id)
    }


}