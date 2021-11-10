package com.hover.runner.transactions.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hover.runner.actions.models.Action
import com.hover.runner.transactions.RunnerTransaction
import com.hover.runner.transactions.usecase.TransactionUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TransactionViewModel(private val useCase: TransactionUseCase) : ViewModel() {
    fun observeTransaction(uuid: String) : LiveData<RunnerTransaction> = useCase.getTransaction(uuid)
    fun observeTransactionsByAction(actionId: String, limit: Int) : LiveData<List<RunnerTransaction>> = useCase.getTransactionsByAction(actionId, limit)


    lateinit var loadingStatusLiveData  : MutableLiveData<Boolean>
    val transactionsLiveData = MutableLiveData<List<RunnerTransaction>>()

    init {
        loadingStatusLiveData.value = false
    }

    fun getAllTransactions() {
        loadingStatusLiveData.value = false
        val deferredTransactions = viewModelScope.async(Dispatchers.IO) { return@async useCase.getAllTransactions() }
        load(deferredTransactions)
    }
    fun getTransactionsByAction(actionId: String)  {
        loadingStatusLiveData.value = false
        val deferredTransactions =  viewModelScope.async(Dispatchers.IO) { return@async useCase.getTransactionsByAction(actionId) }
        load(deferredTransactions)
    }

    private fun load(deferredActions : Deferred<List<RunnerTransaction>>) {
        viewModelScope.launch (Dispatchers.Main) {
            transactionsLiveData.postValue(deferredActions.await())
            loadingStatusLiveData.postValue(true)
        }
    }

}