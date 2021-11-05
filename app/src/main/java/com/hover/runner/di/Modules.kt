package com.hover.runner.di

import com.hover.runner.actions.usecase.ActionRepoInterfaceImpl
import com.hover.runner.actions.usecase.ActionUseCaseImpl
import com.hover.runner.actions.viewmodel.ActionViewModel
import com.hover.runner.login.usecase.LoginUseCaseImpl
import com.hover.runner.login.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        LoginViewModel(LoginUseCaseImpl(get()))

        val actionUseCaseImpl = ActionUseCaseImpl(ActionRepoInterfaceImpl( get() ))
        ActionViewModel(actionUseCaseImpl)
    }
}