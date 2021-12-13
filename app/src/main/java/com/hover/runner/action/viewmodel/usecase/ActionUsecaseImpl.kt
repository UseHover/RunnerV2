package com.hover.runner.action.viewmodel.usecase

import com.hover.runner.action.models.Action
import com.hover.runner.action.models.ActionDetails
import com.hover.runner.action.repo.ActionRepoInterface
import com.hover.runner.filter.filter_actions.model.ActionFilterParameters
import java.util.*

class ActionUseCaseImpl(private val actionRepo: ActionRepoInterface) : ActionUseCase {
	override suspend fun loadAll(): List<Action> {
		return actionRepo.getAllActions()
	}

	override suspend fun getAction(id: String): Action {
		return actionRepo.getAction(id)
	}

	override suspend fun getActionDetails(id: String): ActionDetails {
		return actionRepo.getActionDetailsById(id)
	}

	override suspend fun getDistinctCountries(): List<String> {
		return actionRepo.getAllActionCountryCodes()
	}

	override suspend fun getDistinctNetworkNames(): List<String> {
		return actionRepo.getAllNetworkNames().distinct()
	}

	override suspend fun getNetworkNames(countryCodes: List<String>): List<String> {
		return actionRepo.getNetworkNames(countryCodes).distinct()
	}

	override fun getFirst(actions: List<Action>): Action {
		return actions[0]
	}

	override fun findActionsWithUncompletedVariables(actions: List<Action>): List<Action> {
		val subList = mutableListOf<Action>()
		actions.forEach {
			if (!it.hasAllVariablesFilled(actionRepo.getContext()) && !it.isSkipped) {
				it.jsonArrayToString = it.steps.toString()
				subList.add(it)
			}
		}
		return subList
	}

	override fun findRunnableActions(actions: List<Action>): List<Action> =
		actions.filter { (it.hasAllVariablesFilled(actionRepo.getContext()) && !it.isSkipped) }

	override fun removeFromList(action: Action, actionList: List<Action>): List<Action> {
		return actionList.filterNot { it.id == action.id }
	}

	override fun canFirstItemSave(actionList: List<Action>): Boolean {
		return if (actionList.isEmpty()) false
		else actionList[0].hasAllVariablesFilled(actionRepo.getContext())
	}


}