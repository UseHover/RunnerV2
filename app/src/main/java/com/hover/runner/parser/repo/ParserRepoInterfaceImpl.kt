package com.hover.runner.parser.repo

import com.hover.runner.actions.repo.ActionRepo
import com.hover.runner.parser.model.Parser
import com.hover.runner.transactions.model.RunnerTransaction
import com.hover.runner.transactions.repo.TransactionRepo

class ParserRepoInterfaceImpl(private val parserRepo: ParserRepo,
                                private val actionRepo: ActionRepo,
                              private val transactionRepo: TransactionRepo) : ParserRepoInterface {

    override suspend fun getParser(id: Int): Parser {
        val p = parserRepo.getParser(id)!!
        val action = actionRepo.getHoverAction(p.actionId)
        return Parser(
            action.transportType,
            action.networkName,
            action.id,
            p.regex,
            p.status,
            "None",
            p.senderNumber
        )
    }

    override suspend fun getTransactions(parserId: Int): List<RunnerTransaction> {
        return transactionRepo.getTransactionsByParser(parserId)
    }
}