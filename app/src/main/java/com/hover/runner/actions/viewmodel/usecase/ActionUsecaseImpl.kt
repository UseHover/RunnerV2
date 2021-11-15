package com.hover.runner.actions.viewmodel.usecase

import android.content.Context
import com.hover.runner.actions.models.Action
import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.repo.ActionRepoInterface

class ActionUseCaseImpl(private val actionRepo: ActionRepoInterface)  : ActionUseCase {
    override suspend fun loadAll(): List<Action> {
        return actionRepo.getAllActions()
    }

    override suspend fun filter(): List<Action> {
        TODO("Not yet implemented")
    }

    override suspend fun getAction(id: String): Action {
        return actionRepo.getAction(id);
    }

    override suspend fun getActionDetails(id: String): ActionDetails {
        return actionRepo.getActionDetailsById(id)
    }

    override fun getFirst(actions: List<Action>): Action {
        return actions[0]
    }

    override fun findActionsWithUncompletedVariables(actions: List<Action>): List<Action> {
        val subList = mutableListOf<Action>()
        actions.forEach {
            if (!it.hasAllVariablesFilled(actionRepo.getContext()) && !it.isSkipped ) {
                it.jsonArrayToString = it.steps.toString()
                subList.add(it)
            }
        }
        return subList
    }

    override fun removeFromList(action: Action, actionList: List<Action>) : List<Action> {
        return actionList.filterNot { it.id == action.id }
    }

    override fun canFirstItemSave(actionList: List<Action>): Boolean {
        return if(actionList.isEmpty()) false
        else actionList[0].hasAllVariablesFilled(actionRepo.getContext())
    }


}