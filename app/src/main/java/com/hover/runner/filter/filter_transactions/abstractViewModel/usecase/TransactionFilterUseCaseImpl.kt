package com.hover.runner.filter.filter_transactions.abstractViewModel.usecase

import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.filter.filter_transactions.repo.TransactionFilterRepoImpl
import com.hover.runner.transaction.model.RunnerTransaction

internal class TransactionFilterUseCaseImpl(private val filterRepoImpl: TransactionFilterRepoImpl) : TransactionFilterUseCase {
	override suspend fun filter(parameters: TransactionFilterParameters): List<RunnerTransaction> {
		return filterRepoImpl.filter(parameters)
	}

}