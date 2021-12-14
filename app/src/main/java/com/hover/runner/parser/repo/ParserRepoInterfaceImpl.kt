package com.hover.runner.parser.repo

import com.hover.runner.database.ActionRepo
import com.hover.runner.parser.model.Parser
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.repo.TransactionRepo

class ParserRepoInterfaceImpl(private val parserRepo: ParserRepo,
                              private val actionRepo: ActionRepo,
                              private val transactionRepo: TransactionRepo) : ParserRepoInterface {

	override suspend fun getParser(actionId: String, parserId: Int): Parser {
		val p = parserRepo.getParser(actionId, parserId)!!
		val action = actionRepo.getHoverAction(p.actionId)
		return Parser(action.transport_type,
		              action.name,
		              action.public_id,
		              p.regex,
		              p.status,
		              "None",
		              p.senderNumber)
	}

	override suspend fun getTransactions(parserId: Int): List<RunnerTransaction> {
		return transactionRepo.getTransactionsByParser(parserId)
	}
}