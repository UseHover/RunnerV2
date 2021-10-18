package com.hover.runner.di

import com.hover.runner.login.usecase.LoginUseCaseImpl
import com.hover.runner.login.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        LoginViewModel(LoginUseCaseImpl(get()))
    }
}