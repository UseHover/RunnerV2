package com.hover.runner.transactionDetails

import androidx.lifecycle.*
import com.hover.runner.actions.ActionRepo
import com.hover.runner.transactions.TransactionsRepo
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.transactions.Transaction
import timber.log.Timber

class TransactionDetailsViewModel(private val repo: TransactionsRepo, private val actionRepo: ActionRepo) : ViewModel() {

	var transaction: LiveData<Transaction> = MutableLiveData();
	var action: LiveData<HoverAction> = MutableLiveData()

	fun load(uuid: String) {
		transaction = repo.getTransaction(uuid);
		action = Transformations.map(transaction, this::loadAction)
	}

	private fun loadAction(t: Transaction): HoverAction {
		Timber.e("Loading action: %s", t.actionId)
		return actionRepo.load(t.actionId)
	}
}