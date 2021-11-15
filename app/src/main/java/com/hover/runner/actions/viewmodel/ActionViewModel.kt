package com.hover.runner.actions.viewmodel

import androidx.lifecycle.*
import com.hover.runner.actions.models.Action
import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.models.StreamlinedSteps
import com.hover.runner.actions.viewmodel.usecase.ActionUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ActionViewModel(private val useCase: ActionUseCase) : ViewModel() {
    lateinit var filterStatus: MutableLiveData<Boolean>
    lateinit var loadingStatusLiveData: MutableLiveData<Boolean>
    lateinit var actions: MutableLiveData<List<Action>>
    lateinit var actionDetailsLiveData: MutableLiveData<ActionDetails>

    //UCV means uncompleted variables
    val actionsWithUCV_LiveData: MutableLiveData<List<Action>> = MutableLiveData()

    init {
        filterStatus.value = false
        loadingStatusLiveData.value = false
        Transformations.map(actions, this::updateUCVList)
    }

    fun getAllActions() {
        loadingStatusLiveData.postValue(false)
        val loadedActions = viewModelScope.async(Dispatchers.IO) {
            return@async useCase.loadAll()
        }

        load(loadedActions)
    }

    fun getActionDetail(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            actionDetailsLiveData.postValue(useCase.getActionDetails(id))
        }
    }


    suspend fun getAction(id: String): Action {
        val deferredAction =
            viewModelScope.async(Dispatchers.IO) { return@async useCase.getAction(id) }
        return deferredAction.await()
    }

    fun filterActions() {
        loadingStatusLiveData.postValue(false)
        val loadedActions = viewModelScope.async(Dispatchers.IO) {
            return@async useCase.filter()
        }
        load(loadedActions)
    }

    private fun load(deferredActions: Deferred<List<Action>>) {
        viewModelScope.launch(Dispatchers.Main) {
            actions.postValue(deferredActions.await())
            loadingStatusLiveData.postValue(true)
        }
    }

    private fun updateUCVList(actions: List<Action>) {
        actionsWithUCV_LiveData.postValue(useCase.findActionsWithUncompletedVariables(actions))
    }

    fun removeFromUCVList(action: Action) {
        val actionList: List<Action>? = actionsWithUCV_LiveData.value
        actionList?.let { actionsWithUCV_LiveData.postValue( useCase.removeFromList(action, it) ) }
    }
    fun getCurrentUCVAction() : Action {
        val actions = actionsWithUCV_LiveData.value!!
        return useCase.getFirst(actions)
    }
    fun canCurrentActionSave() : Boolean {
        val actions = actionsWithUCV_LiveData.value
        return useCase.canFirstItemSave(actions!!)
    }

}