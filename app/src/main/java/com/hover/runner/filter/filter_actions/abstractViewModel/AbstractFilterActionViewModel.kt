package com.hover.runner.filter.filter_actions.abstractViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hover.runner.action.models.Action
import com.hover.runner.filter.filter_actions.abstractViewModel.usecase.ActionFilterUseCase
import com.hover.runner.filter.filter_actions.model.ActionFilterParameters
import com.hover.runner.sim.viewmodel.SimViewModel
import com.hover.runner.sim.viewmodel.usecase.SimUseCase
import com.hover.sdk.transactions.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class AbstractFilterActionViewModel(private val filterUseCase: ActionFilterUseCase,
                                             simUseCase: SimUseCase) : SimViewModel(simUseCase) {

	val actionFilterParametersMutableLiveData: MutableLiveData<ActionFilterParameters> = MutableLiveData()
	val filteredActionsMutableLiveData: MutableLiveData<List<Action>> = MutableLiveData()
	val filterHasLoadedLiveData: MutableLiveData<Boolean> = MutableLiveData()

	init {
		actionFilterParametersMutableLiveData.value = ActionFilterParameters.getDefault()
		filterHasLoadedLiveData.value = true
	}

	fun getActionFilterParam(): ActionFilterParameters {
		return actionFilterParametersMutableLiveData.value!!
	}

	fun filter_getActions(): List<Action> {
		return with(filteredActionsMutableLiveData) {
			if (value == null) ArrayList()
			else value!!
		}
	}

	fun runFilter(actionFilterParameters: ActionFilterParameters) {
		if (!actionFilterParameters.isDefault()) {
			filterHasLoadedLiveData.postValue(false)
			val deferredActions = viewModelScope.async(Dispatchers.IO) {
				return@async filterUseCase.filter(actionFilterParameters)
			}

			viewModelScope.launch(Dispatchers.Main) {
				val actionList = deferredActions.await()
				filteredActionsMutableLiveData.postValue(actionList)
				filterHasLoadedLiveData.postValue(true)
			}
		}else filter_reset()
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
		filterParam.includeActionsWithNoTransaction = shouldInclude
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