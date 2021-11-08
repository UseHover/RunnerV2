package com.hover.runner.transactions.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hover.runner.transactions.RunnerTransaction
import com.hover.runner.transactions.usecase.TransactionUseCase

class TransactionViewModel(private val useCase: TransactionUseCase) : ViewModel() {
    val transactionsLiveData: LiveData<List<RunnerTransaction>> = useCase.getAllTransactions()

    fun getTransaction(uuid: String) : LiveData<RunnerTransaction> {
        return useCase.getTransaction(uuid)
    }

    fun getTransactionsByAction(actionId: String) : LiveData<List<RunnerTransaction>> {
        return useCase.getTransactionsByAction(actionId)
    }



}