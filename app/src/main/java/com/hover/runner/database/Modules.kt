package com.hover.runner.di

import com.hover.runner.actionDetails.ActionDetailViewModel
import com.hover.runner.actions.ActionRepo
import com.hover.runner.actions.ActionsViewModel
import com.hover.runner.database.AppDatabase
import com.hover.runner.testRuns.TestRunRepo
import com.hover.runner.login.viewmodel.LoginViewModel
import com.hover.runner.login.viewmodel.usecase.LoginUseCaseImpl
import com.hover.runner.parser.ParserRepo
import com.hover.runner.parser.ParserViewModel
import com.hover.runner.settings.SimsRepo
import com.hover.runner.settings.SimsViewModel
import com.hover.runner.testRunCreation.NewRunViewModel
import com.hover.runner.testRunning.RunningViewModel
import com.hover.runner.testRuns.RunsViewModel
import com.hover.runner.transactionDetails.TransactionDetailsViewModel
import com.hover.runner.transactions.TransactionsRepo
import com.hover.runner.transactions.TransactionsViewModel
import com.hover.sdk.database.HoverRoomDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
	viewModel {
		LoginViewModel(LoginUseCaseImpl(get()))
	}

	viewModel {
		TransactionsViewModel(get(), get(), get())
	}

	viewModel {
		TransactionDetailsViewModel(get(), get())
	}

	viewModel {
		ParserViewModel(get(), get(), get())
	}

	viewModel {
		ActionsViewModel(get(), get(), get())
	}

	viewModel {
		NewRunViewModel(get(), get(), get())
	}

	viewModel {
		RunsViewModel(get(), get(), get(), get())
	}

	viewModel {
		RunningViewModel(get(), get(), get())
	}

	viewModel {
		ActionDetailViewModel(get(), get(), get())
	}

	viewModel {
		SimsViewModel(get(), get())
	}
}

val dataModule = module(createdAtStart = true) {
	single { AppDatabase.getInstance(get()) }
	single { HoverRoomDatabase.getInstance(get()) }
	single { ActionRepo(get()) }
	single { TestRunRepo(get()) }
	single { TransactionsRepo(get()) }
	single { ParserRepo(get()) }
	single { SimsRepo(get()) }
}