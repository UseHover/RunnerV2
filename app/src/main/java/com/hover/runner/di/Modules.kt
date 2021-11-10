package com.hover.runner.di

import com.hover.runner.actions.repo.ActionRepo
import com.hover.runner.actions.repo.ActionRepoInterfaceImpl
import com.hover.runner.actions.usecase.ActionUseCaseImpl
import com.hover.runner.actions.viewmodel.ActionViewModel
import com.hover.runner.database.AppDatabase
import com.hover.runner.login.usecase.LoginUseCaseImpl
import com.hover.runner.login.viewmodel.LoginViewModel
import com.hover.runner.parser.repo.ParserRepo
import com.hover.runner.transactions.repo.TransactionRepo
import com.hover.runner.transactions.repo.TransactionRepoInterfaceImpl
import com.hover.runner.transactions.usecase.TransactionUseCaseImpl
import com.hover.runner.transactions.viewmodel.TransactionViewModel
import com.hover.sdk.database.HoverRoomDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        LoginViewModel(LoginUseCaseImpl(get()))

        val actionUseCaseImpl = ActionUseCaseImpl(ActionRepoInterfaceImpl( get(), get(), get()))
        ActionViewModel(actionUseCaseImpl)

        val transactionUseCaseImpl = TransactionUseCaseImpl(TransactionRepoInterfaceImpl( get() ))
        TransactionViewModel(transactionUseCaseImpl)

    }
}

val dataModule = module(createdAtStart = true) {
    single { AppDatabase.getInstance(get()) }
    single { HoverRoomDatabase.getInstance(get()) }
    single { ActionRepo(get()) }
    single { TransactionRepo(get()) }
    single { ParserRepo(get()) }
}