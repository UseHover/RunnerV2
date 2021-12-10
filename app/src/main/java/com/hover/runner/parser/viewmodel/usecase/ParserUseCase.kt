package com.hover.runner.parser.viewmodel.usecase

import com.hover.runner.parser.model.Parser
import com.hover.runner.transaction.model.RunnerTransaction

interface ParserUseCase {
	suspend fun getParser(actionId: String, parserId: Int): Parser
	suspend fun getTransactions(parserId: Int): List<RunnerTransaction>
}