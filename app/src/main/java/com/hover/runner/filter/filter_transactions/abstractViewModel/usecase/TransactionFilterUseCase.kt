package com.hover.runner.filter.filter_transactions.abstractViewModel.usecase

import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.model.RunnerTransaction

interface TransactionFilterUseCase {
	suspend fun filter(parameters: TransactionFilterParameters) : List<RunnerTransaction>
}