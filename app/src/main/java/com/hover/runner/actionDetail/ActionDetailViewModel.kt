package com.hover.runner.actionDetail

import androidx.lifecycle.*
import com.hover.runner.database.ActionRepo
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.repo.TransactionRepo
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActionDetailViewModel(private val actionRepo: ActionRepo, private val transactionRepo: TransactionRepo) : ViewModel() {

	val action: MutableLiveData<HoverAction> = MutableLiveData()
	val transactions: LiveData<List<RunnerTransaction>>
	val successCount: LiveData<Int>
	val failedCount: LiveData<Int>
	val pendingCount: LiveData<Int>

	init {
		transactions = Transformations.switchMap(action, this::getActionTransactions)
		successCount = Transformations.switchMap(action) { getStatusCount(it, Transaction.SUCCEEDED) }
		failedCount = Transformations.switchMap(action){ getStatusCount(it, Transaction.FAILED) }
		pendingCount = Transformations.switchMap(action) { getStatusCount(it, Transaction.PENDING) }
	}

	fun loadAction(id: String) {
		viewModelScope.launch(Dispatchers.IO) {
			action.postValue(actionRepo.getHoverAction(id))
		}
	}

	private fun getActionTransactions(action: HoverAction) : LiveData<List<RunnerTransaction>> {
		return transactionRepo.getTransactionsByAction(action.public_id, T_LIMIT)
	}

	private fun getStatusCount(action: HoverAction, status: String) : LiveData<Int> {
		return transactionRepo.getCountByStatus(action.public_id, status)
	}

	companion object {
		const val T_LIMIT = 10
	}
}