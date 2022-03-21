package com.hover.runner.parser

import androidx.lifecycle.*
import com.hover.runner.actions.ActionRepo
import com.hover.runner.transactions.TransactionsRepo
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.parsers.HoverParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParserViewModel(private val parserRepo: ParserRepo, private val actionRepo: ActionRepo, private val transactionsRepo: TransactionsRepo) : ViewModel() {
	val parser: MutableLiveData<HoverParser> = MutableLiveData()
	val action: LiveData<HoverAction>
//	val transactions: LiveData<List<Transaction>>

	init {
		action = Transformations.map(parser, this::getAction)
//		transactions = Transformations.switchMap(parser, this::getTransactions)
	}

	fun setParser(parserId: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			parser.postValue(parserRepo.getParser(parserId))
		}
	}

	private fun getAction(parser: HoverParser) : HoverAction {
		return actionRepo.load(parser.actionId)
	}

//	private fun getTransactions(parser: HoverParser) : LiveData<List<RunnerTransaction>> {
//		return transactionsRepo.getTransactionsByParser(parser.id)
//	}

}