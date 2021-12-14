package com.hover.runner.filter.filter_actions.abstractViewModel

import androidx.lifecycle.MutableLiveData
import com.hover.runner.actions.StyledAction
import com.hover.runner.filter.filter_actions.model.ActionFilterParameters
import com.hover.runner.sim.viewmodel.SimViewModel
import com.hover.runner.sim.viewmodel.usecase.SimUseCase
import com.hover.sdk.transactions.Transaction

abstract class AbstractFilterActionViewModel(simUseCase: SimUseCase) : SimViewModel(simUseCase) {
	val actionFilterParametersMutableLiveData: MutableLiveData<ActionFilterParameters> = MutableLiveData()
	val filteredActionsMutableLiveData: MutableLiveData<List<StyledAction>> = MutableLiveData()

	init {
		actionFilterParametersMutableLiveData.value = ActionFilterParameters.getDefault()
	}

	fun getActionFilterParam(): ActionFilterParameters {
		return actionFilterParametersMutableLiveData.value!!
	}

	fun filter_actionsTotal(): Int {
		return with(filteredActionsMutableLiveData) { if (value == null) 0 else value!!.size }
	}

	fun filter_getActions(): List<StyledAction> {
		return with(filteredActionsMutableLiveData) {
			if (value == null) ArrayList()
			else value!!
		}
	}

	fun filter_reset() {
		actionFilterParametersMutableLiveData.postValue(ActionFilterParameters.getDefault())
		filteredActionsMutableLiveData.postValue(ArrayList())
	}

	fun filter_byActionSearch(value: String) {
		val filterParam = getActionFilterParam()
		filterParam.actionId = if (isARootCode(value)) "" else value
		filterParam.actionRootCode = if (isARootCode(value)) value else ""
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_UpdateActionIds(actionIds: List<String>) {
		val filterParam = getActionFilterParam()
		filterParam.actionIdList = actionIds
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_UpdateCountryCodeList(countryCodes: List<String>) {
		val filterParam = getActionFilterParam()
		filterParam.countryCodeList = countryCodes
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_UpdateNetworkNameList(networkNames: List<String>) {
		val filterParam = getActionFilterParam()
		filterParam.networkNameList = networkNames
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_UpdateCategoryList(catogories: List<String>) {
		val filterParam = getActionFilterParam()
		filterParam.categoryList = catogories
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_UpdateDateRange(start: Long, end: Long) {
		val filterParam = getActionFilterParam()
		filterParam.startDate = start
		filterParam.endDate = end
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_IncludeSucceededTransactions(shouldInclude: Boolean) {
		val filterParam = getActionFilterParam()
		if (shouldInclude) filterParam.transactionSuccessful = Transaction.SUCCEEDED
		else filterParam.transactionSuccessful = ""
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_IncludePendingTransactions(shouldInclude: Boolean) {
		val filterParam = getActionFilterParam()
		if (shouldInclude) filterParam.transactionPending = Transaction.PENDING
		else filterParam.transactionPending = ""
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_IncludeFailedTransactions(shouldInclude: Boolean) {
		val filterParam = getActionFilterParam()
		if (shouldInclude) filterParam.transactionFailed = Transaction.FAILED
		else filterParam.transactionFailed = ""
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_IncludeActionsWithNoTransaction(shouldInclude: Boolean) {
		val filterParam = getActionFilterParam()
		filterParam.hasNoTransaction = shouldInclude
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_IncludeActionsWithParsers(shouldInclude: Boolean) {
		val filterParam = getActionFilterParam()
		filterParam.hasParser = shouldInclude
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	fun filter_ShowOnlyActionsWithSimPresent(shouldShow: Boolean) {
		val filterParam = getActionFilterParam()
		filterParam.onlyWithSimPresent = shouldShow
		actionFilterParametersMutableLiveData.postValue(filterParam)
	}

	private fun isARootCode(value: String): Boolean {
		return value.first().toString() == "*" && value.last().toString() == "#"
	}
}