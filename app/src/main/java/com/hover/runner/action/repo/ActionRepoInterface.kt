package com.hover.runner.action.repo

import android.content.Context
import com.hover.runner.action.models.Action
import com.hover.runner.action.models.ActionDetails
import com.hover.runner.filter_actions.model.ActionFilterParam

interface ActionRepoInterface {
    suspend fun getAllActions(): List<Action>
    suspend fun getActionDetailsById(id: String): ActionDetails
    suspend fun getAction(id: String): Action
    suspend fun getAllActionCountryCodes() : List<String>
    suspend fun getNetworkNames(countryCodes: List<String>) : List<String>
    suspend fun getAllNetworkNames() : List<String>
    suspend fun filter(actionFilterParam: ActionFilterParam): List<Action>
    fun getContext(): Context
}