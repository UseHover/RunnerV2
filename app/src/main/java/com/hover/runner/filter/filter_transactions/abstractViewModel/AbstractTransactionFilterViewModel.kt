package com.hover.runner.filter.filter_transactions.abstractViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hover.runner.filter.filter_transactions.abstractViewModel.usecase.TransactionFilterUseCase
import com.hover.runner.filter.filter_transactions.model.TransactionFilterParameters
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class AbstractTransactionFilterViewModel(private val useCase: TransactionFilterUseCase) : ViewModel() {
	val filterLoadingStatusLiveData: MutableLiveData<Boolean> = MutableLiveData()
	val transactionFilterParametersMutableLiveData: MutableLiveData<TransactionFilterParameters> = MutableLiveData()
	val filteredTransactionsMutableLiveData: MutableLiveData<List<RunnerTransaction>> = MutableLiveData()

	init {
		transactionFilterParametersMutableLiveData.value = TransactionFilterParameters.getDefault()
	}

	fun runFilter(transactionFilterParameters: TransactionFilterParameters) {
		if (!transactionFilterParameters.isDefault()) {
			filterLoadingStatusLiveData.postValue(false)

			val deferredTransactions = viewModelScope.async(Dispatchers.IO) {
				return@async useCase.filter(transactionFilterParameters)
			}

			viewModelScope.launch(Dispatchers.Main) {
				val transactionList = deferredTransactions.await()
				filteredTransactionsMutableLiveData.postValue(transactionList)
				filterLoadingStatusLiveData.postValue(true)
			}
		} else filter_reset()
	}
	fun reloadFilterTransactions() {
		runFilter(transactionFilterParametersMutableLiveData.value!!)
	}

	fun getTransactionFilterParam(): TransactionFilterParameters {
		return transactionFilterParametersMutableLiveData.value!!
	}

	fun filter_getTransactions(): List<RunnerTransaction> {
		return with(filteredTransactionsMutableLiveData) {
			if (value == null) ArrayList()
			else value!!
		}
	}

	fun filter_reset() {
		transactionFilterParametersMutableLiveData.postValue(TransactionFilterParameters.getDefault())
		filteredTransactionsMutableLiveData.postValue(ArrayList())
	}

	fun filter_UpdateActionIds(actionIds: List<String>) {
		Timber.i("updated ids size is {${actionIds.size}}")
		val filterParam = getTransactionFilterParam()
		filterParam.actionIdList = actionIds
		transactionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_UpdateCountryCodeList(countryCodes: List<String>) {
		val filterParam = getTransactionFilterParam()
		filterParam.countryCodeList = countryCodes
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
		if (shouldInclude) filterParam.successful = Transaction.SUCCEEDED
		else filterParam.successful = ""
		transactionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_IncludePendingTransactions(shouldInclude: Boolean) {
		val filterParam = getTransactionFilterParam()
		if (shouldInclude) filterParam.pending = Transaction.PENDING
		else filterParam.pending = ""
		transactionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_IncludeFailedTransactions(shouldInclude: Boolean) {
		val filterParam = getTransactionFilterParam()
		if (shouldInclude) filterParam.failed = Transaction.FAILED
		else filterParam.failed = ""
		transactionFilterParametersMutableLiveData.postValue(filterParam)
	}
}