package com.hover.runner.di

import com.hover.runner.actionDetail.ActionDetailViewModel
import com.hover.runner.database.ActionRepo
import com.hover.runner.actions.ActionsViewModel
import com.hover.runner.database.AppDatabase
import com.hover.runner.login.viewmodel.LoginViewModel
import com.hover.runner.login.viewmodel.usecase.LoginUseCaseImpl
import com.hover.runner.parser.ParserRepo
import com.hover.runner.parser.ParserViewModel
import com.hover.runner.sim.repo.SimRepo
import com.hover.runner.sim.repo.SimRepoInterfaceImpl
import com.hover.runner.sim.viewmodel.SimViewModel
import com.hover.runner.sim.viewmodel.usecase.SimUseCaseImpl
import com.hover.runner.transaction.repo.TransactionRepo
import com.hover.runner.transaction.repo.TransactionRepoInterfaceImpl
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.transaction.viewmodel.usecase.TransactionUseCaseImpl
import com.hover.sdk.database.HoverRoomDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
	viewModel {
		LoginViewModel(LoginUseCaseImpl(get()))
	}

	viewModel {
		val transactionUseCaseImpl =
			TransactionUseCaseImpl(TransactionRepoInterfaceImpl(get(), get(), get()))
		TransactionViewModel(transactionUseCaseImpl)
	}

	viewModel {
		ParserViewModel(get(), get(), get())
	}

	viewModel {
		ActionsViewModel(get(), get())
	}

	viewModel {
		ActionDetailViewModel(get(), get(), get())
	}

	viewModel {
		val simUseCaseImpl = SimUseCaseImpl(SimRepoInterfaceImpl(get()))
		SimViewModel(simUseCaseImpl)
	}
}

val dataModule = module(createdAtStart = true) {
	single { AppDatabase.getInstance(get()) }
	single { HoverRoomDatabase.getInstance(get()) }
	single { ActionRepo(get()) }
	single { TransactionRepo(get()) }
	single { ParserRepo(get()) }
	single { SimRepo(get()) }
}