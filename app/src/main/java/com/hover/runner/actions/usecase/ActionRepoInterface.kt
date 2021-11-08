package com.hover.runner.actions.usecase

import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.models.Action
import com.hover.sdk.actions.HoverAction

interface ActionRepoInterface {
    suspend fun getAllActions() : List<Action>
    suspend fun getActionDetailsById(id: String) : ActionDetails
    suspend fun getAction(id: String) : Action
}