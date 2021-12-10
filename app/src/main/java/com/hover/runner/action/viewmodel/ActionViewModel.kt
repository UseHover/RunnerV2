package com.hover.runner.action.viewmodel

import androidx.lifecycle.*
import com.hover.runner.action.models.Action
import com.hover.runner.action.models.ActionDetails
import com.hover.runner.action.viewmodel.usecase.ActionUseCase
import com.hover.runner.filter.filter_actions.abstractViewModel.AbstractFilterActionViewModel
import com.hover.runner.filter.filter_actions.model.ActionFilterParameters
import com.hover.runner.sim.viewmodel.usecase.SimUseCase
import com.hover.sdk.sims.SimInfo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class ActionViewModel(private val useCase: ActionUseCase, simUseCase: SimUseCase) :
	AbstractFilterActionViewModel(simUseCase) {

	//MutableLiveData
	private val filterStatus: MutableLiveData<Boolean> = MutableLiveData()
	val loadingStatusLiveData: MutableLiveData<Boolean> = MutableLiveData()
	val actions: MutableLiveData<List<Action>> = MutableLiveData()
	val actionDetailsLiveData: MutableLiveData<ActionDetails> = MutableLiveData()
	val actionsParentTotalLiveData: MutableLiveData<Int> = MutableLiveData()
	val countryListMutableLiveData: MutableLiveData<List<String>> = MutableLiveData()

	private val allNetworksLiveData: MutableLiveData<List<String>> = MutableLiveData()
	val networksInPresentSimCountryNamesLiveData: MutableLiveData<List<String>> = MutableLiveData()
	val networksOutsidePresentSimCountryNamesLiveData: MutableLiveData<List<String>> =
		MutableLiveData()

	val actionsWithUCV_LiveData: MutableLiveData<List<Action>> =
		MutableLiveData()  //UCV means uncompleted variables
	val actionsWithCompletedVariables: MutableLiveData<List<Action>> = MutableLiveData()

	//MediatorLiveData
	val allActions_toFind_BadActions_MediatorLiveData: MediatorLiveData<List<Action>> =
		MediatorLiveData()
	val badActions_toFind_GoodActions_MediatorLiveData: MediatorLiveData<List<Action>> =
		MediatorLiveData()
	val filterParameters_toFind_FilteredActions_MediatorLiveData: MediatorLiveData<ActionFilterParameters> =
		MediatorLiveData()

	val allNetworks_toLoad_countryCodes_MediatorLiveData: MediatorLiveData<List<String>> =
		MediatorLiveData()
	val countryCodes_toFind_ItsNetworks_MediatorLiveData: MediatorLiveData<List<SimInfo>> =
		MediatorLiveData()
	val networksWithinCountry_toFind_networksOutsideCountry_MediatorLiveData: MediatorLiveData<List<String>> =
		MediatorLiveData()

	init {
		filterStatus.value = false
		loadingStatusLiveData.value = false
		actionsParentTotalLiveData.value = 0
		setupMediators()
	}

	private fun setupMediators() {
		allActions_toFind_BadActions_MediatorLiveData.addSource(actions,
		                                                        this::updateActionsWithUCVList)
		badActions_toFind_GoodActions_MediatorLiveData.addSource(actionsWithUCV_LiveData,
		                                                         this::updateRunnableActions)
		filterParameters_toFind_FilteredActions_MediatorLiveData.addSource(
			actionFilterParametersMutableLiveData,
			this::runFilter)

		allNetworks_toLoad_countryCodes_MediatorLiveData.addSource(allNetworksLiveData,
		                                                           this::loadSimCountries)
		countryCodes_toFind_ItsNetworks_MediatorLiveData.addSource(
			presentSimCountryCodes_MutableLiveData,
			this::loadNetworksInCountry)
		networksWithinCountry_toFind_networksOutsideCountry_MediatorLiveData.addSource(
			networksInPresentSimCountryNamesLiveData,
			this::loadNetworksOutsideCountry)
	}

	fun loadNetworkNames() {
		viewModelScope.launch(Dispatchers.IO) { allNetworksLiveData.postValue(useCase.getDistinctNetworkNames()) }
	}

	private fun loadSimCountries(ignored: List<String>) {
		getSimCountries()
	}

	private fun loadNetworksInCountry(countryCodes: List<String>) {
		viewModelScope.launch(Dispatchers.IO) {
			networksInPresentSimCountryNamesLiveData.postValue(useCase.getNetworkNames(countryCodes))
		}
	}

	fun getNetworksInCountries() : List<String> {
		return networksInPresentSimCountryNamesLiveData.value ?: emptyList()
	}

	private fun loadNetworksOutsideCountry(networksWithinCountry: List<String>) {
		val allNetworkNames: List<String> = allNetworksLiveData.value!!
		val outsideCountry = allNetworkNames - networksWithinCountry.toSet()
		networksOutsidePresentSimCountryNamesLiveData.postValue(outsideCountry)
	}

	fun loadDistinctCountries() {
		viewModelScope.launch(Dispatchers.IO) { countryListMutableLiveData.postValue(useCase.getDistinctCountries()) }
	}

	fun getAllActions() {
		loadingStatusLiveData.postValue(false)
		val loadedActions = viewModelScope.async(Dispatchers.IO) {
			return@async useCase.loadAll()
		}

		load(loadedActions)
	}

	fun getActionDetail(id: String) {
		viewModelScope.launch(Dispatchers.Main) {
			actionDetailsLiveData.postValue(useCase.getActionDetails(id))
		}
	}

	suspend fun getAction(id: String): Action {
		val deferredAction =
			viewModelScope.async(Dispatchers.IO) { return@async useCase.getAction(id) }
		return deferredAction.await()
	}

	fun updateActionsLiveData(newActions: List<Action>) {
		actions.postValue(newActions)
	}

	private fun runFilter(actionFilterParameters: ActionFilterParameters) {
		if (!actionFilterParameters.isDefault()) {
			loadingStatusLiveData.postValue(false)
			val deferredActions = viewModelScope.async(Dispatchers.IO) {
				return@async useCase.filter(actionFilterParameters)
			}

			viewModelScope.launch(Dispatchers.Main) {
				val actionList = deferredActions.await()
				filteredActionsMutableLiveData.postValue(actionList)
				loadingStatusLiveData.postValue(true)
			}
		}else filter_reset()
	}

	private fun load(deferredActions: Deferred<List<Action>>) {
		viewModelScope.launch(Dispatchers.Main) {
			val actionList = deferredActions.await()
			actions.postValue(actionList)
			loadingStatusLiveData.postValue(true)
			updateParentActionsTotal(actionList.size)
		}
	}

	private fun updateParentActionsTotal(newActionListSize: Int) {
		val currentTotalActionsSize: Int = actionsParentTotalLiveData.value!!
		if (newActionListSize > currentTotalActionsSize) {
			actionsParentTotalLiveData.postValue(newActionListSize)
		}
	}

	private fun updateActionsWithUCVList(actions: List<Action>) {
		Timber.i("ucv list triggered")
		actionsWithUCV_LiveData.postValue(useCase.findActionsWithUncompletedVariables(actions))
	}

	fun hasBadActions(): Boolean {
		val badActions: List<Action>? = actionsWithUCV_LiveData.value
		return badActions?.isNotEmpty() ?: false
	}

	fun removeFromUCVList(action: Action) {
		val actionList: List<Action>? = actionsWithUCV_LiveData.value
		actionList?.let {
			Timber.i("remove from ucv list triggered")
			actionsWithUCV_LiveData.postValue(useCase.removeFromList(action, it))
		}
	}

	fun getCurrentUCVAction(): Action {
		val actions = actionsWithUCV_LiveData.value!!
		return useCase.getFirst(actions)
	}

	fun canCurrentActionSave(): Boolean {
		val actions = actionsWithUCV_LiveData.value
		return useCase.canFirstItemSave(actions!!)
	}

	fun getRunnableActions(): List<Action> {
		return actionsWithCompletedVariables.value ?: ArrayList()
	}

	private fun updateRunnableActions(badActions: List<Action>) {
		val actionList: List<Action> = if (badActions.isEmpty()) actions.value!!
		else useCase.findRunnableActions(actions.value!!)
		actionsWithCompletedVariables.postValue(actionList)
	}

}