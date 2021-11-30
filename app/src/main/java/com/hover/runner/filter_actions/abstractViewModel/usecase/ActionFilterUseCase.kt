package com.hover.runner.filter_actions.abstractViewModel.usecase

import com.hover.runner.action.models.Action
import com.hover.runner.filter_actions.models.ActionFilterParams

interface ActionFilterUseCase {
    fun filterActions(actionFilterParams: ActionFilterParams) : List<Action>
}