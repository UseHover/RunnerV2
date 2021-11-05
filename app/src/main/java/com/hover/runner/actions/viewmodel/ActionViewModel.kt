package com.hover.runner.actions.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hover.runner.actions.models.Action
import com.hover.runner.actions.usecase.ActionUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ActionViewModel(private val useCase: ActionUseCase) : ViewModel() {
    lateinit var filterStatus: MutableLiveData<Boolean>
    lateinit var isLoadingCompleted  : MutableLiveData<Boolean>
    lateinit var actions: MutableLiveData<List<Action>>

    init {
        filterStatus.value = false
        isLoadingCompleted.value = false
    }

    fun loadAllActions() {
        isLoadingCompleted.postValue(false)
        val loadedActions = viewModelScope.async(Dispatchers.IO) {
            return@async useCase.loadAll()
        }
        load(loadedActions)
    }

    fun filterActions() {
        isLoadingCompleted.postValue(false)
        val loadedActions = viewModelScope.async(Dispatchers.IO) {
            return@async useCase.filter()
        }
        load(loadedActions)
    }

    private fun load(deferredActions : Deferred<List<Action>>) {
        viewModelScope.launch (Dispatchers.Main) {
            actions.postValue(deferredActions.await())
            isLoadingCompleted.postValue(true)
        }
    }

}