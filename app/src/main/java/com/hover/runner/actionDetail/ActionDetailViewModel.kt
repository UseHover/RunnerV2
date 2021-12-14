package com.hover.runner.actionDetail

import androidx.lifecycle.*
import com.hover.runner.database.ActionRepo
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.repo.TransactionRepo
import com.hover.sdk.actions.HoverAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActionDetailViewModel(private val actionRepo: ActionRepo, private val transactionRepo: TransactionRepo) : ViewModel() {

	val action: MutableLiveData<HoverAction> = MutableLiveData()
	val transactions: LiveData<List<RunnerTransaction>>

	init {
		transactions = Transformations.switchMap(action, this::getActionTransactions)
	}

	fun loadAction(id: String) {
		viewModelScope.launch(Dispatchers.IO) {
			action.postValue(actionRepo.getHoverAction(id))
		}
	}

	private fun getActionTransactions(action: HoverAction) : LiveData<List<RunnerTransaction>> {
		return transactionRepo.getTransactionsByAction(action.public_id, 10)
	}
}