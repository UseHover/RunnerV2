package com.hover.runner.testRuns

import android.app.Application
import androidx.lifecycle.*
import com.hover.runner.actions.ActionRepo
import com.hover.runner.transactions.TransactionsRepo
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RunsViewModel(private val application: Application, private val runRepo: TestRunRepo, private val actionRepo: ActionRepo, private val transactionRepo: TransactionsRepo) : ViewModel() {
	var runs: LiveData<List<TestRun>> = runRepo.getAll()

	var run: MutableLiveData<TestRun> = MutableLiveData()
	var actions: LiveData<List<HoverAction>>
	var transactions: LiveData<List<Transaction>>

	init {
		transactions = Transformations.switchMap(run, this::getTransactions)
		actions = Transformations.map(run, this::getActions)
	}

	fun load(id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			run.postValue(runRepo.load(id))
		}
	}

	private fun getActions(r: TestRun): List<HoverAction> {
		return actionRepo.getHoverActions(r.action_id_list.toTypedArray())
	}

	private fun getTransactions(r: TestRun): LiveData<List<Transaction>> {
		return transactionRepo.getTransactions(r.transaction_uuid_list.toTypedArray())
	}

	fun deleteRun() {
		viewModelScope.launch(Dispatchers.IO) {
			run.value?.let { runRepo.delete(it, application) }
		}
	}
}