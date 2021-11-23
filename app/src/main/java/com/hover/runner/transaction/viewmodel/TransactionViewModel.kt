package com.hover.runner.transaction.viewmodel

import androidx.lifecycle.*
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.transaction.model.TransactionDetailsInfo
import com.hover.runner.transaction.model.TransactionDetailsMessages
import com.hover.runner.transaction.viewmodel.usecase.TransactionUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TransactionViewModel(private val useCase: TransactionUseCase) : ViewModel() {

    fun observeTransaction(uuid: String): LiveData<RunnerTransaction> = useCase.getTransaction(uuid)
    fun observeTransactionsByAction(
        actionId: String,
        limit: Int
    ): LiveData<List<RunnerTransaction>> = useCase.getTransactionsByAction(actionId, limit)

    val loadingStatusLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val transactionsLiveData: MutableLiveData<List<RunnerTransaction>> = MutableLiveData()

    init {
        loadingStatusLiveData.value = false
    }

    fun getAllTransactions() {
        loadingStatusLiveData.value = false
        val deferredTransactions =
            viewModelScope.async(Dispatchers.IO) { return@async useCase.getAllTransactions() }
        load(deferredTransactions)
    }

    fun getTransactionsByAction(actionId: String) {
        loadingStatusLiveData.value = false
        val deferredTransactions = viewModelScope.async(Dispatchers.IO) {
            return@async useCase.getTransactionsByAction(actionId)
        }
        load(deferredTransactions)
    }

    private fun load(deferredActions: Deferred<List<RunnerTransaction>>) {
        viewModelScope.launch(Dispatchers.Main) {
            transactionsLiveData.postValue(deferredActions.await())
            loadingStatusLiveData.postValue(true)
        }
    }

    fun observeDeviceInfo(transaction: RunnerTransaction): LiveData<List<TransactionDetailsInfo>> {
        return liveData {
            viewModelScope.async { return@async useCase.getDeviceInfo(transaction) }.await()
        }
    }

    fun observeAboutInfo(transaction: RunnerTransaction): LiveData<List<TransactionDetailsInfo>> {
        return liveData {
            viewModelScope.async(Dispatchers.Main) { return@async useCase.getAboutInfo(transaction) }.await()
        }
    }

    fun observeDebugInfo(transaction: RunnerTransaction): LiveData<List<TransactionDetailsInfo>> {
        return liveData {
            viewModelScope.async(Dispatchers.Main) { return@async useCase.getDebugInfo(transaction) }.await()
        }
    }

    fun observeTransactionMessages(transaction: RunnerTransaction): LiveData<List<TransactionDetailsMessages>> {
        return liveData {
            viewModelScope.async(Dispatchers.Main) { return@async useCase.getMessagesInfo(transaction) }.await()
        }
    }


}