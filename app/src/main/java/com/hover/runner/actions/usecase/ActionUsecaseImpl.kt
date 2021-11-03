package com.hover.runner.actions.usecase

import androidx.lifecycle.MutableLiveData
import com.hover.runner.actions.model.ActionModel

class ActionUseCaseImpl  : ActionUseCase {
    override suspend fun loadAll(): List<ActionModel> {
        TODO("Not yet implemented")
    }

    override suspend fun filter(): List<ActionModel> {
        TODO("Not yet implemented")
    }


}