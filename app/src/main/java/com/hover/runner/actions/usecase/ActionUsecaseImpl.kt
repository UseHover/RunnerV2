package com.hover.runner.actions.usecase

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.hover.runner.actions.model.ActionModel

class ActionUseCaseImpl(private val context: Context)  : ActionUseCase {
    override suspend fun loadAll(): List<ActionModel> {
        TODO("Not yet implemented")
    }

    override suspend fun filter(): List<ActionModel> {
        TODO("Not yet implemented")
    }


}