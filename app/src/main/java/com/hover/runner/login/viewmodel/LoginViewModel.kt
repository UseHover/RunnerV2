package com.hover.runner.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hover.runner.login.viewmodel.usecase.LoginUseCase
import com.hover.runner.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val useCase: LoginUseCase) : ViewModel() {
	val loginLiveData: MutableLiveData<Resource<Int>> = MutableLiveData()
	val validationLiveData: MutableLiveData<Resource<Int>> = MutableLiveData()

	fun doLogin(email: String, password: String) {
		viewModelScope.launch(Dispatchers.IO) {
			loginLiveData.postValue(Resource.Loading())
			loginLiveData.postValue(useCase.login(email, password))
		}
	}

	fun validate(email: String, password: String) {
		viewModelScope.launch(Dispatchers.Main) {
			validationLiveData.postValue(useCase.validate(email, password))
		}
	}
}