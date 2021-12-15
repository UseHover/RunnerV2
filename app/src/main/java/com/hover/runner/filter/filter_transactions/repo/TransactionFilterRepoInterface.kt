package com.hover.runner.filter.filter_transactions.repo

import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.model.RunnerTransaction

interface TransactionFilterRepoInterface {
	suspend fun filter(params: TransactionFilterParameters): List<RunnerTransaction>
}