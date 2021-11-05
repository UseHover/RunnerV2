package com.hover.runner.actions.usecase

import com.hover.runner.actions.models.Action

class ActionUseCaseImpl(private val actionRepo: ActionRepoInterface)  : ActionUseCase {
    override suspend fun loadAll(): List<Action> {
        return actionRepo.getAllActionsFromHover()
    }

    override suspend fun filter(): List<Action> {
        TODO("Not yet implemented")
    }


}