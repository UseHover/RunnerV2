package com.hover.runner.database

import com.hover.runner.actions.models.ActionDetailsModel
import com.hover.runner.actions.models.ActionModel
import com.hover.runner.actions.usecase.ActionRepoInterface
import com.hover.sdk.database.HoverRoomDatabase

class DatabaseRepo(sdkDB: HoverRoomDatabase) : ActionRepoInterface {

    override suspend fun getAllActionsFromHover(): List<ActionModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getActionDetailsById() : ActionDetailsModel {
        TODO("Not yet implemented")
    }
}