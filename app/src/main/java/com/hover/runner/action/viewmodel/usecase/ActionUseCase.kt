package com.hover.runner.action.viewmodel.usecase

import androidx.lifecycle.LiveData
import com.hover.runner.action.models.Action
import com.hover.runner.action.models.ActionDetails
import com.hover.runner.filter_actions.model.ActionFilterParam

interface ActionUseCase {
    suspend fun loadAll(): List<Action>
    suspend fun filter(actionFilterParam: ActionFilterParam): List<Action>
    suspend fun getAction(id: String): Action
    suspend fun getActionDetails(id: String): ActionDetails
    suspend fun getDistinctCountries() : List<String>
    suspend fun getDistinctNetworkNames() : List<String>
    suspend fun getNetworkNames(countryCodes: List<String>) : List<String>

    fun getFirst(actions: List<Action>): Action
    fun findActionsWithUncompletedVariables(actions: List<Action>): List<Action>
    fun findRunnableActions(actions: List<Action>): List<Action>
    fun removeFromList(action: Action, actionList: List<Action>): List<Action>
    fun canFirstItemSave(actionList: List<Action>): Boolean
}