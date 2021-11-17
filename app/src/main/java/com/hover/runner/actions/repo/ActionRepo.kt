package com.hover.runner.actions.repo

import com.hover.sdk.actions.HoverAction
import com.hover.sdk.database.HoverRoomDatabase

class ActionRepo(private val sdkDB: HoverRoomDatabase) {
    suspend fun getAllActionsFromHover(): List<HoverAction> {
        return sdkDB.actionDao().all
    }

    suspend fun getHoverAction(id: String): HoverAction {
        return sdkDB.actionDao().getAction(id)
    }
}