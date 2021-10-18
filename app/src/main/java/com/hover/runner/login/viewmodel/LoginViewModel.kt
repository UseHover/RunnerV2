package com.hover.runner.login.viewmodel

import androidx.lifecycle.*
import com.hover.runner.login.usecase.LoginUseCase
import com.hover.runner.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val useCase: LoginUseCase) : ViewModel() {
    val loginLiveData : MutableLiveData<Resource<Int>> = MutableLiveData()
    fun doLogin(email : String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginLiveData.postValue(Resource.Loading())
            loginLiveData.postValue(useCase.login(email, password))
        }
    }
}