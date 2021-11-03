package com.hover.runner.actions.usecase

import com.hover.runner.actions.model.ActionModel

interface ActionUseCase {
    suspend fun loadAll() : List<ActionModel>
    suspend fun filter() : List<ActionModel>
}