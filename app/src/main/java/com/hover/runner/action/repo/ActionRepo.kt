package com.hover.runner.action.repo

import com.hover.runner.action.models.Action
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.database.HoverRoomDatabase
import timber.log.Timber

class ActionRepo(private val sdkDB: HoverRoomDatabase) {
	suspend fun getAllHoverActions(): List<HoverAction> {
		return sdkDB.actionDao().all
	}

	suspend fun getHoverAction(id: String): HoverAction {
		return sdkDB.actionDao().getAction(id)
	}

	fun getAllActionsCountryCodes(): List<String> {
		return sdkDB.actionDao().allCountryCodes
	}

	suspend fun getNetworkNamesByCountryCodes(countryCodes: Array<String>): List<String> {
		return sdkDB.actionDao().getNetworkNamesByCountryCodes(countryCodes)
	}

	suspend fun getAllNetworkNames(): List<String> {
		return sdkDB.actionDao().allNetworkNames
	}

	suspend fun getHoverActions(ids: Array<String>) : List<HoverAction>{
		return sdkDB.actionDao().getActions(ids)
	}
	suspend fun  getAllHoverActionIds() : Array<String>{
		return sdkDB.actionDao().allActionIds
	}
	suspend fun filterByActionId(subList:Array<String>, actionId: String) : Array<String> {
		return sdkDB.actionDao().filterByActionId(subList, actionId)
	}
	suspend fun filterByRootCode(subList: Array<String>, actionRootCode: String) : Array<String> {
		return sdkDB.actionDao().filterByRootCode(subList, actionRootCode)
	}

	suspend fun filterByActionIds(subList: Array<String>, actionIdList: Array<String>) : Array<String> {
		return sdkDB.actionDao().filterByActionIdList(subList, actionIdList)
	}
	suspend fun filterByActionIds(actionIdList: Array<String>) : Array<String> {
		return sdkDB.actionDao().filterByActionIdList(actionIdList)
	}

	suspend fun filterByCountries(subList: Array<String>, countryCodes: Array<String>) : Array<String> {
		return sdkDB.actionDao().filterByCountry(subList, countryCodes)
	}
	suspend fun filterByCountries(countryCodes: Array<String>) : Array<String> {
		return sdkDB.actionDao().filterByCountry(countryCodes)
	}
}