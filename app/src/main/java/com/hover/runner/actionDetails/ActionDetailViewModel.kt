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
	val transactions: LiveData<List<Transaction>>
	val parsers: LiveData<List<HoverParser>>

	val successCount: LiveData<Int>
	val failedCount: LiveData<Int>
	val pendingCount: LiveData<Int>

	init {
		transactions = Transformations.switchMap(action, this::getActionTransactions)
		parsers = Transformations.map(action, this::getActionParsers)

		successCount = Transformations.map(transactions){ getStatusCount(it, Transaction.SUCCEEDED); }
		pendingCount = Transformations.map(transactions){ getStatusCount(it, Transaction.PENDING); }
		failedCount = Transformations.map(transactions){ getStatusCount(it, Transaction.FAILED); }
	}

	fun loadAction(id: String) {
		viewModelScope.launch(Dispatchers.IO) {
			action.postValue(actionRepo.load(id))
		}
	}

	private fun getActionTransactions(action: HoverAction) : LiveData<List<Transaction>> {
		return transactionsRepo.getTransactionsByAction(action.public_id)
	}

	private fun getActionParsers(action: HoverAction) : List<HoverParser> {
		return parserRepo.getParsersByActionId(action.public_id)
	}

	private fun getStatusCount(transactions: List<Transaction>, status: String) : Int {
		return transactions.count { it.status == status }
	}

	companion object {
		const val T_LIMIT = 10
	}
}