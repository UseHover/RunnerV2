package com.hover.runner.filter.filter_actions.abstractViewModel.usecase

import com.hover.runner.action.models.Action
import com.hover.runner.filter.filter_actions.model.ActionFilterParameters

interface ActionFilterUseCase {
	suspend fun filter(actionFilterParameters: ActionFilterParameters): List<Action>
}