package com.hover.runner.di

import com.hover.runner.action.repo.ActionRepo
import com.hover.runner.action.repo.ActionRepoInterfaceImpl
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.action.viewmodel.usecase.ActionUseCaseImpl
import com.hover.runner.database.AppDatabase
import com.hover.runner.filter.filter_actions.abstractViewModel.usecase.ActionFilterUseCaseImpl
import com.hover.runner.filter.filter_actions.repo.ActionFilterRepoImpl
import com.hover.runner.filter.filter_transactions.abstractViewModel.usecase.TransactionFilterUseCaseImpl
import com.hover.runner.filter.filter_transactions.repo.TransactionFilterRepoImpl
import com.hover.runner.login.viewmodel.LoginViewModel
import com.hover.runner.login.viewmodel.usecase.LoginUseCaseImpl
import com.hover.runner.parser.repo.ParserRepo
import com.hover.runner.parser.repo.ParserRepoInterfaceImpl
import com.hover.runner.parser.viewmodel.ParserViewModel
import com.hover.runner.parser.viewmodel.usecase.ParserUseCaseImpl
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
		val filterUseCaseImpl = TransactionFilterUseCaseImpl(TransactionFilterRepoImpl(get(), get(), get()))
		val transactionUseCaseImpl = TransactionUseCaseImpl(TransactionRepoInterfaceImpl(get(), get(), get()))
		TransactionViewModel(transactionUseCaseImpl, filterUseCaseImpl)
	}

	viewModel {
		val parserUseCaseImpl = ParserUseCaseImpl(ParserRepoInterfaceImpl(get(), get(), get()))
		ParserViewModel(parserUseCaseImpl)
	}

	viewModel {
		val filterUseCaseImpl = ActionFilterUseCaseImpl(ActionFilterRepoImpl(get(), get(), get(), get(), get()))
		val simUseCaseImpl = SimUseCaseImpl(SimRepoInterfaceImpl(get()))

		val actionUseCaseImpl = ActionUseCaseImpl(ActionRepoInterfaceImpl(get(), get(), get(), get()))
		ActionViewModel(actionUseCaseImpl, filterUseCaseImpl, simUseCaseImpl)
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