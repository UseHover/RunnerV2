package com.hover.runner.filter.filter_actions.abstractViewModel.usecase

import com.hover.runner.action.models.Action
import com.hover.runner.filter.filter_actions.repo.ActionFilterRepoInterface
import com.hover.runner.filter.filter_actions.model.ActionFilterParameters

class ActionFilterUseCaseImpl(private val filterRepo: ActionFilterRepoInterface) : ActionFilterUseCase {
	override suspend fun filter(actionFilterParameters: ActionFilterParameters): List<Action> {
		return filterRepo.filter(actionFilterParameters)
	}
}