package com.hover.runner.actions.repo

import android.content.Context
import com.hover.runner.database.AppDatabase
import com.hover.sdk.actions.HoverAction

class ActionRepo(private val context: Context) {

    suspend fun getAllActionsFromHover(): List<HoverAction> {
        return HoverAction.loadAll(context)
    }

    suspend fun getHoverAction(id: String): HoverAction {
        return HoverAction.load(id, context)
    }
}