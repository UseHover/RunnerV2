package com.hover.runner.parser

import androidx.lifecycle.*
import com.hover.runner.actions.ActionRepo
import com.hover.runner.transactions.TransactionsRepo
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.parsers.HoverParser
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParserViewModel(private val parserRepo: ParserRepo, private val actionRepo: ActionRepo, private val transactionsRepo: TransactionsRepo) : ViewModel() {
	val parser: MutableLiveData<HoverParser> = MutableLiveData()
	private var parserId: Int = 0
	val action: LiveData<HoverAction>
//	val transactions: LiveData<List<Transaction>>

	init {
		action = Transformations.map(parser, this::getAction)
//		transactions = Transformations.switchMap(parser, this::getTransactions)
	}

	fun setParser(id: Int) {
		parserId = id
		viewModelScope.launch(Dispatchers.IO) {
			parser.postValue(parserRepo.getParser(parserId))
		}
	}

	private fun getAction(parser: HoverParser) : HoverAction {
		return actionRepo.load(parser.actionId)
	}

	fun getTransactions() : LiveData<List<Transaction>> {
		return transactionsRepo.getAllTransactionsByParser(parserId.toString())
	}

}