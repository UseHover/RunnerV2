package com.hover.runner.actions.usecase

import com.hover.runner.actions.models.Action

interface ActionUseCase {
    suspend fun loadAll() : List<Action>
    suspend fun filter() : List<Action>
}