package com.hover.runner.sim.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hover.runner.sim.viewmodel.usecase.SimUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class SimViewModel(private val useCase: SimUseCase) : ViewModel() {
    val presentSimsLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val presentSimCountryCodes_MutableLiveData : MutableLiveData<List<String>> = MutableLiveData()

    fun getPresentSims() {
        viewModelScope.launch(Dispatchers.IO) {
            presentSimsLiveData.postValue(useCase.getPresentSimNames())
        }
    }

    fun getSimCountries() {
        viewModelScope.launch(Dispatchers.IO) {
            presentSimCountryCodes_MutableLiveData.postValue(useCase.getSimCountryCodes())
        }
    }
}