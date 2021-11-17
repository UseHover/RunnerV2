package com.hover.runner.parser.repo

import com.hover.runner.parser.model.Parser
import com.hover.runner.transaction.model.RunnerTransaction

interface ParserRepoInterface {
    suspend fun getParser(id: Int): Parser
    suspend fun getTransactions(parserId: Int): List<RunnerTransaction>
}