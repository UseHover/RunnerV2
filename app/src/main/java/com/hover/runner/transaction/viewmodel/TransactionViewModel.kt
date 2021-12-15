package com.hover.runner.transaction.viewmodel

import androidx.lifecycle.*
import com.hover.runner.filter.filter_transactions.abstractViewModel.AbstractTransactionFilterViewModel
import com.hover.runner.filter.filter_transactions.abstractViewModel.usecase.TransactionFilterUseCase
import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.model.TransactionDetailsInfo
import com.hover.runner.transaction.model.TransactionDetailsMessages
import com.hover.runner.transaction.viewmodel.usecase.TransactionUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TransactionViewModel(private val useCase: TransactionUseCase, filterUseCase: TransactionFilterUseCase) :
	AbstractTransactionFilterViewModel(filterUseCase) {

	fun observeTransaction(uuid: String): LiveData<RunnerTransaction> = useCase.getTransaction(uuid)
	fun observeTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>> = useCase.getTransactionsByAction(actionId, limit)

	private val allTransactionsHolderLiveData : LiveData<List<RunnerTransaction>> = useCase.getAllTransactionsLiveData()
	val highestTransactionsCountLiveData: MutableLiveData<Int> = MutableLiveData()

	val loadingStatusLiveData: MutableLiveData<Boolean> = MutableLiveData()
	val transactionsLiveData: MutableLiveData<List<RunnerTransaction>> = MutableLiveData()

	val aboutInfoMutableLiveData: MutableLiveData<List<TransactionDetailsInfo>> = MutableLiveData()
	val deviceInfoMutableLiveData: MutableLiveData<List<TransactionDetailsInfo>> = MutableLiveData()
	val debugInfoMutableLiveData: MutableLiveData<List<TransactionDetailsInfo>> = MutableLiveData()
	val messagesInfoLiveData: MutableLiveData<List<TransactionDetailsMessages>> = MutableLiveData()

	val distinctCategoryMutableLiveData: MutableLiveData<List<String>> = MutableLiveData()

	//MediatorLiveData
	val filter_Parameters_toFind_FilteredTransactions_MediatorLiveData: MediatorLiveData<TransactionFilterParameters> = MediatorLiveData()
	val allTransactionsHolder_toRefresh_transactionsOrFilterList_MediatorLiveData: MediatorLiveData<List<RunnerTransaction>> = MediatorLiveData()

	init {
		loadingStatusLiveData.value = false
		highestTransactionsCountLiveData.value = 0

		filter_Parameters_toFind_FilteredTransactions_MediatorLiveData.addSource(
			transactionFilterParametersMutableLiveData,
			this::runFilter)

		allTransactionsHolder_toRefresh_transactionsOrFilterList_MediatorLiveData.addSource(
			allTransactionsHolderLiveData,
			this::refreshTransactions)

	}

	fun loadDistinctCategories() {
		viewModelScope.launch(Dispatchers.IO) { distinctCategoryMutableLiveData.postValue(useCase.getDistinctTransactionCategories()) }
	}

	fun getAllTransactions() {
		loadingStatusLiveData.value = false
		val deferredTransactions =
			viewModelScope.async(Dispatchers.IO) { return@async useCase.getAllTransactions() }
		load(deferredTransactions)
	}


	private fun updateParentTransactionsTotal(newTransactionListSize: Int) {
		val currentTotalTransactionsSize: Int = highestTransactionsCountLiveData.value!!
		if (newTransactionListSize > currentTotalTransactionsSize) {
			highestTransactionsCountLiveData.postValue(newTransactionListSize)
		}
	}

	fun updateTransactionsLiveData(newTransactions: List<RunnerTransaction>) {
		transactionsLiveData.postValue(newTransactions)
	}
	fun getHighestTransactionsCount() : Int {
		return highestTransactionsCountLiveData.value!!
	}


	fun getTransactionsByAction(actionId: String) {
		val parameters: TransactionFilterParameters = TransactionFilterParameters.getDefault()
		val newList = mutableListOf<String>()
		newList.add(actionId)

		parameters.actionIdList = newList
		runFilter(parameters)
	}

	private fun load(deferredActions: Deferred<List<RunnerTransaction>>) {
		viewModelScope.launch(Dispatchers.Main) {
			val transactionList = deferredActions.await()
			transactionsLiveData.postValue(transactionList)
			loadingStatusLiveData.postValue(true)
			updateParentTransactionsTotal(transactionList.size)
		}
	}

	fun loadDeviceInfo(transaction: RunnerTransaction) {
		viewModelScope.launch(Dispatchers.IO) {
			deviceInfoMutableLiveData.postValue(useCase.getDeviceInfo(transaction))
		}
	}

	fun loadAboutInfo(transaction: RunnerTransaction) {
		viewModelScope.launch(Dispatchers.IO) {
			aboutInfoMutableLiveData.postValue(useCase.getAboutInfo(transaction))
		}
	}

	fun loadDebugInfo(transaction: RunnerTransaction) {
		viewModelScope.launch(Dispatchers.IO) {
			debugInfoMutableLiveData.postValue(useCase.getDebugInfo(transaction))
		}
	}

	fun loadTransactionMessages(transaction: RunnerTransaction) {
		viewModelScope.launch(Dispatchers.IO) {
			messagesInfoLiveData.postValue(useCase.getMessagesInfo(transaction))
		}
	}
	private fun refreshTransactions(newList: List<RunnerTransaction>) {
		transactionsLiveData.value?.let {
			val isInFilterMode : Boolean = it.size < highestTransactionsCountLiveData.value!!

			if(isInFilterMode) reloadFilterTransactions()
			else getAllTransactions()
		}
	}


}