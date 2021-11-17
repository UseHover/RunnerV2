package com.hover.runner.actions.repo

import android.content.Context
import com.hover.runner.actions.models.Action
import com.hover.runner.actions.models.ActionDetails

interface ActionRepoInterface {
    suspend fun getAllActions(): List<Action>
    suspend fun getActionDetailsById(id: String): ActionDetails
    suspend fun getAction(id: String): Action
    fun getContext(): Context
}