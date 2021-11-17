package com.hover.runner.parser.viewmodel.usecase

import com.hover.runner.parser.model.Parser
import com.hover.runner.transactions.model.RunnerTransaction

interface ParserUseCase {
    suspend fun getParser(id: Int): Parser
    suspend fun getTransactions(parserId: Int): List<RunnerTransaction>
}