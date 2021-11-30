package com.hover.runner.filter_actions.abstractViewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hover.runner.action.models.Action
import com.hover.runner.filter_actions.abstractViewModel.usecase.ActionFilterUseCase
import com.hover.runner.filter_actions.models.ActionFilterParams
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class AbstractFilterActionViewModel(private val filterUseCase: ActionFilterUseCase) : ViewModel() {
    val actionFilterParamsMutableLiveData : MutableLiveData<ActionFilterParams> = MutableLiveData()

    val actionFilterParamMediatorLiveData : MediatorLiveData<ActionFilterParams> = MediatorLiveData()
    private val filteredActionsMutableLiveData : MutableLiveData<List<Action>> = MutableLiveData()
    init {
        actionFilterParamsMutableLiveData.value = ActionFilterParams.getDefault()
    }

    private fun getActionFilter() : ActionFilterParams {
        return actionFilterParamsMutableLiveData.value!!
    }

    fun runFilter(actionFilterParams: ActionFilterParams) {
        viewModelScope.launch(Dispatchers.Main) {
            filteredActionsMutableLiveData.postValue(filterUseCase.filterActions(actionFilterParams))
        }
    }

    fun filter_UpdateActionIds(actionIds : List<String>) {
        val actionFilter = getActionFilter()
        actionFilter.actionIdList = actionIds
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }

    fun filter_UpdateCountryCodeList(countryCodes: List<String>) {
        val actionFilter = getActionFilter()
        actionFilter.countryCodeList = countryCodes
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }

    fun filter_UpdateNetworkNameList(networkNames: List<String>) {
        val actionFilter = getActionFilter()
        actionFilter.networkNameList = networkNames
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }

    fun filter_UpdateCategoryList(catogories: List<String>) {
        val actionFilter = getActionFilter()
        actionFilter.categoryList = catogories
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }

    fun filter_UpdateDateRange(start: Long, end: Long) {
        val actionFilter = getActionFilter()
        actionFilter.startDate = start
        actionFilter.endDate = end
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }

    fun filter_IncludeSucceededTransactions(shouldInclude: Boolean) {
        val actionFilter = getActionFilter()
        if(shouldInclude) actionFilter.transactionSuccessful = Transaction.SUCCEEDED
        else actionFilter.transactionSuccessful = ""
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }
    fun filter_IncludePendingTransactions(shouldInclude: Boolean) {
        val actionFilter = getActionFilter()
        if(shouldInclude) actionFilter.transactionPending = Transaction.PENDING
        else actionFilter.transactionPending = ""
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }
    fun filter_IncludeFailedTransactions(shouldInclude: Boolean) {
        val actionFilter = getActionFilter()
        if(shouldInclude) actionFilter.transactionFailed = Transaction.FAILED
        else actionFilter.transactionFailed = ""
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }
    fun filter_IncludeActionsWithNoTransaction(shouldInclude: Boolean) {
        val actionFilter = getActionFilter()
        actionFilter.hasNoTransaction = shouldInclude
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }
    fun filter_IncludeActionsWithParsers(shouldInclude: Boolean) {
        val actionFilter = getActionFilter()
        actionFilter.hasParser = shouldInclude
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }
    fun filter_ShowOnlyActionsWithSimPresent(shouldShow: Boolean) {
        val actionFilter = getActionFilter()
        actionFilter.onlyWithSimPresent = shouldShow
        actionFilterParamsMutableLiveData.postValue(actionFilter)
    }
}