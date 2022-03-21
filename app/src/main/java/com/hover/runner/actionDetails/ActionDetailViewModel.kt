package com.hover.runner.actionDetails

import androidx.lifecycle.*
import com.hover.runner.actions.ActionRepo
import com.hover.runner.parser.ParserRepo
import com.hover.runner.transactions.TransactionsRepo
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.parsers.HoverParser
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActionDetailViewModel(private val actionRepo: ActionRepo, private val transactionsRepo: TransactionsRepo, private val parserRepo: ParserRepo) : ViewModel() {

	val action: MutableLiveData<HoverAction> = MutableLiveData()
	val parsers: LiveData<List<HoverParser>>

	init {
		parsers = Transformations.map(action, this::getActionParsers)
	}

	fun loadAction(id: String) {
		viewModelScope.launch(Dispatchers.IO) {
			action.postValue(actionRepo.load(id))
		}
	}

	fun getActionTransactions(actionId: String) : LiveData<List<Transaction>> {
			return transactionsRepo.getLiveTransactionsByAction(actionId)
	}

	private fun getActionParsers(action: HoverAction) : List<HoverParser> {
		return parserRepo.getParsersByActionId(action.public_id)
	}

	companion object {
		const val T_LIMIT = 10
	}
}