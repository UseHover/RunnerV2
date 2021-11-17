package com.hover.runner.action.repo

import android.content.Context
import com.hover.runner.action.models.Action
import com.hover.runner.action.models.ActionDetails

interface ActionRepoInterface {
    suspend fun getAllActions(): List<Action>
    suspend fun getActionDetailsById(id: String): ActionDetails
    suspend fun getAction(id: String): Action
    fun getContext(): Context
}