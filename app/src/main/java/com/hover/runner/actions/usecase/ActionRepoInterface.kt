package com.hover.runner.actions.usecase

import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.models.Action

interface ActionRepoInterface {
    suspend fun getAllActionsFromHover() : List<Action>
    suspend fun getActionDetailsById(id: String) : ActionDetails
    suspend fun getAction(id: String) : Action
}