package com.hover.runner.filter.filter_transactions.abstractViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.sdk.transactions.Transaction

abstract class AbstractTransactionFilterViewModel : ViewModel() {
    val transactionFilterParametersMutableLiveData : MutableLiveData<TransactionFilterParameters> = MutableLiveData()
    val filteredTransactionsMutableLiveData : MutableLiveData<List<RunnerTransaction>> = MutableLiveData()
    val filter_getParameters : TransactionFilterParameters? = transactionFilterParametersMutableLiveData.value

    init {
        transactionFilterParametersMutableLiveData.value = TransactionFilterParameters.getDefault()
    }

    private fun getTransactionFilterParam() : TransactionFilterParameters {
        return transactionFilterParametersMutableLiveData.value!!
    }

    fun filter_transactionsTotal() : Int {
        return with (filteredTransactionsMutableLiveData) { if (value == null) 0 else value!!.size }
    }

    fun filter_getTransactions() : List<RunnerTransaction> {
        return with (filteredTransactionsMutableLiveData) {
            if(value == null) ArrayList()
            else value!!
        }
    }
    fun filter_reset() {
        transactionFilterParametersMutableLiveData.postValue(TransactionFilterParameters.getDefault())
        filteredTransactionsMutableLiveData.postValue(ArrayList())
    }

    fun filter_UpdateActionIds(actionIds : List<String>) {
        val filterParam = getTransactionFilterParam()
        filterParam.actionIdList = actionIds
        transactionFilterParametersMutableLiveData.postValue(filterParam)
    }

    fun filter_UpdateCountryNameList(countryNames: List<String>) {
        val filterParam = getTransactionFilterParam()
        filterParam.countryNameList = countryNames
        transactionFilterParametersMutableLiveData.postValue(filterParam)
    }

    fun filter_UpdateNetworkNameList(networkNames: List<String>) {
        val filterParam = getTransactionFilterParam()
        filterParam.networkNameList = networkNames
        transactionFilterParametersMutableLiveData.postValue(filterParam)
    }

    fun filter_UpdateDateRange(start: Long, end: Long) {
        val filterParam = getTransactionFilterParam()
        filterParam.startDate = start
        filterParam.endDate = end
        transactionFilterParametersMutableLiveData.postValue(filterParam)
    }

    fun filter_IncludeSucceededTransactions(shouldInclude: Boolean) {
        val filterParam = getTransactionFilterParam()
        if(shouldInclude) filterParam.successful = Transaction.SUCCEEDED
        else filterParam.successful = ""
        transactionFilterParametersMutableLiveData.postValue(filterParam)
    }
    fun filter_IncludePendingTransactions(shouldInclude: Boolean) {
        val filterParam = getTransactionFilterParam()
        if(shouldInclude) filterParam.pending = Transaction.PENDING
        else filterParam.pending = ""
        transactionFilterParametersMutableLiveData.postValue(filterParam)
    }
    fun filter_IncludeFailedTransactions(shouldInclude: Boolean) {
        val filterParam = getTransactionFilterParam()
        if(shouldInclude) filterParam.failed = Transaction.FAILED
        else filterParam.failed = ""
        transactionFilterParametersMutableLiveData.postValue(filterParam)
    }
}