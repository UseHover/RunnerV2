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
    fun observeTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>> = useCase.getTransactionsByAction(actionId, limit)

    val loadingStatusLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val transactionsLiveData: MutableLiveData<List<RunnerTransaction>> = MutableLiveData()

    val aboutInfoMutableLiveData : MutableLiveData<List<TransactionDetailsInfo>> = MutableLiveData()
    val deviceInfoMutableLiveData : MutableLiveData<List<TransactionDetailsInfo>> = MutableLiveData()
    val debugInfoMutableLiveData : MutableLiveData<List<TransactionDetailsInfo>> = MutableLiveData()
    val messagesInfoLiveData : MutableLiveData<List<TransactionDetailsMessages>> = MutableLiveData()

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

    fun loadDeviceInfo(transaction: RunnerTransaction){
        viewModelScope.launch(Dispatchers.IO) {
            deviceInfoMutableLiveData.postValue(useCase.getDeviceInfo(transaction))
        }
    }

    fun loadAboutInfo(transaction: RunnerTransaction){
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


}