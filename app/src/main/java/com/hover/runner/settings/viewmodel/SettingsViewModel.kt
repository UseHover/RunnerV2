package com.hover.runner.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hover.runner.settings.usecase.SettingsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(private val useCase: SettingsUseCase) : ViewModel() {
    val presentSimsLiveData: MutableLiveData<List<String>> = MutableLiveData()

    fun getPresentSims() {
        viewModelScope.launch(Dispatchers.IO) {
            presentSimsLiveData.postValue(useCase.getPresentSimNames())
        }
    }
}