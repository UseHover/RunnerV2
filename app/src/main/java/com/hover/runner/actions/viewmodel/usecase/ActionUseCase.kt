package com.hover.runner.actions.viewmodel.usecase

import com.hover.runner.actions.models.Action
import com.hover.runner.actions.models.ActionDetails

interface ActionUseCase {
    suspend fun loadAll() : List<Action>
    suspend fun filter() : List<Action>
    suspend fun getAction(id: String) : Action
    suspend fun getActionDetails(id: String) : ActionDetails
}