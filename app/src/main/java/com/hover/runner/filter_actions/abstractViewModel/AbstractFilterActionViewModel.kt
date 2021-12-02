package com.hover.runner.filter_actions.abstractViewModel

import androidx.lifecycle.MutableLiveData
import com.hover.runner.action.models.Action
import com.hover.runner.filter_actions.model.ActionFilterParam
import com.hover.runner.sim.viewmodel.SimViewModel
import com.hover.runner.sim.viewmodel.usecase.SimUseCase
import com.hover.sdk.transactions.Transaction

abstract class AbstractFilterActionViewModel(simUseCase: SimUseCase) : SimViewModel(simUseCase) {
    val actionFilterParamMutableLiveData : MutableLiveData<ActionFilterParam> = MutableLiveData()

    val filteredActionsMutableLiveData : MutableLiveData<List<Action>> = MutableLiveData()
    val filter_getParam : ActionFilterParam? = actionFilterParamMutableLiveData.value

    init {
        actionFilterParamMutableLiveData.value = ActionFilterParam.getDefault()
    }

    private fun getActionFilterParam() : ActionFilterParam {
        return actionFilterParamMutableLiveData.value!!
    }

    fun filter_actionsTotal() : Int {
        return with (filteredActionsMutableLiveData) { if (value == null) 0 else value!!.size }
    }

    fun filter_getActions() : List<Action> {
        return with (filteredActionsMutableLiveData) {
            if(value == null) ArrayList()
            else value!!
        }
    }

    fun filter_reset() {
        actionFilterParamMutableLiveData.postValue(ActionFilterParam.getDefault())
        filteredActionsMutableLiveData.postValue(ArrayList())
    }

    fun filter_byActionSearch(value: String) {
        val filterParam = getActionFilterParam()
        filterParam.actionId = if(isARootCode(value)) "" else value
        filterParam.actionRootCode = if(isARootCode(value)) value else ""
        actionFilterParamMutableLiveData.postValue(filterParam)
    }

    fun filter_UpdateActionIds(actionIds : List<String>) {
        val filterParam = getActionFilterParam()
        filterParam.actionIdList = actionIds
        actionFilterParamMutableLiveData.postValue(filterParam)
    }

    fun filter_UpdateCountryNameList(countryNames: List<String>) {
        val filterParam = getActionFilterParam()
        filterParam.countryNameList = countryNames
        actionFilterParamMutableLiveData.postValue(filterParam)
    }

    fun filter_UpdateNetworkNameList(networkNames: List<String>) {
        val filterParam = getActionFilterParam()
        filterParam.networkNameList = networkNames
        actionFilterParamMutableLiveData.postValue(filterParam)
    }

    fun filter_UpdateCategoryList(catogories: List<String>) {
        val filterParam = getActionFilterParam()
        filterParam.categoryList = catogories
        actionFilterParamMutableLiveData.postValue(filterParam)
    }

    fun filter_UpdateDateRange(start: Long, end: Long) {
        val filterParam = getActionFilterParam()
        filterParam.startDate = start
        filterParam.endDate = end
        actionFilterParamMutableLiveData.postValue(filterParam)
    }

    fun filter_IncludeSucceededTransactions(shouldInclude: Boolean) {
        val filterParam = getActionFilterParam()
        if(shouldInclude) filterParam.transactionSuccessful = Transaction.SUCCEEDED
        else filterParam.transactionSuccessful = ""
        actionFilterParamMutableLiveData.postValue(filterParam)
    }
    fun filter_IncludePendingTransactions(shouldInclude: Boolean) {
        val filterParam = getActionFilterParam()
        if(shouldInclude) filterParam.transactionPending = Transaction.PENDING
        else filterParam.transactionPending = ""
        actionFilterParamMutableLiveData.postValue(filterParam)
    }
    fun filter_IncludeFailedTransactions(shouldInclude: Boolean) {
        val filterParam = getActionFilterParam()
        if(shouldInclude) filterParam.transactionFailed = Transaction.FAILED
        else filterParam.transactionFailed = ""
        actionFilterParamMutableLiveData.postValue(filterParam)
    }
    fun filter_IncludeActionsWithNoTransaction(shouldInclude: Boolean) {
        val filterParam = getActionFilterParam()
        filterParam.hasNoTransaction = shouldInclude
        actionFilterParamMutableLiveData.postValue(filterParam)
    }
    fun filter_IncludeActionsWithParsers(shouldInclude: Boolean) {
        val filterParam = getActionFilterParam()
        filterParam.hasParser = shouldInclude
        actionFilterParamMutableLiveData.postValue(filterParam)
    }
    fun filter_ShowOnlyActionsWithSimPresent(shouldShow: Boolean) {
        val filterParam = getActionFilterParam()
        filterParam.onlyWithSimPresent = shouldShow
        actionFilterParamMutableLiveData.postValue(filterParam)
    }
    private fun isARootCode(value: String) : Boolean {
        return value.first().toString() =="*" && value.last().toString() == "#"
    }
}