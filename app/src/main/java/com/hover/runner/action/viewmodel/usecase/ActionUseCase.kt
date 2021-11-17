package com.hover.runner.action.viewmodel.usecase

import com.hover.runner.action.models.Action
import com.hover.runner.action.models.ActionDetails

interface ActionUseCase {
    suspend fun loadAll(): List<Action>
    suspend fun filter(): List<Action>
    suspend fun getAction(id: String): Action
    suspend fun getActionDetails(id: String): ActionDetails
    fun getFirst(actions: List<Action>): Action
    fun findActionsWithUncompletedVariables(actions: List<Action>): List<Action>
    fun findRunnableActions(actions: List<Action>): List<Action>
    fun removeFromList(action: Action, actionList: List<Action>): List<Action>
    fun canFirstItemSave(actionList: List<Action>): Boolean
}