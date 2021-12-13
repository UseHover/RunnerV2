package com.hover.runner.filter.filter_actions.repo

import com.hover.runner.action.models.Action
import com.hover.runner.filter.filter_actions.model.ActionFilterParameters

interface ActionFilterRepoInterface {
	suspend fun filter(actionFilterParameters: ActionFilterParameters): List<Action>
}