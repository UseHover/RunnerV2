package com.hover.runner.parser.viewmodel.usecase

import com.hover.runner.parser.model.Parser
import com.hover.runner.parser.repo.ParserRepoInterface
import com.hover.runner.transactions.model.RunnerTransaction

class ParserUseCaseImpl(private val parserRepoInterface: ParserRepoInterface) : ParserUseCase {
    override suspend fun getParser(id: Int): Parser {
        return parserRepoInterface.getParser(id)
    }

    override suspend fun getTransactions(parserId: Int): List<RunnerTransaction> {
        return parserRepoInterface.getTransactions(parserId)
    }
}