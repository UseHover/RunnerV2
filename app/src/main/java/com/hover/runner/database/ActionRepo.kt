package com.hover.runner.database

import androidx.lifecycle.LiveData
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.database.HoverRoomDatabase
import timber.log.Timber

class ActionRepo(private val sdkDB: HoverRoomDatabase) {
	suspend fun getAllActionsFromHover(): List<HoverAction> {
		return sdkDB.actionDao().all
	}

	suspend fun getAllActions(): LiveData<List<HoverAction>> {
		return sdkDB.actionDao().allLive
	}

	fun getHoverAction(id: String): HoverAction {
		return sdkDB.actionDao().getAction(id)
	}

	fun getAllActionsCountryCodes(): List<String> {
		return sdkDB.actionDao().allCountryCodes
	}

//	suspend fun filterHoverAction(actionId: String, actionRootCode: String,
//	                              actionIdList: List<String>, countryCodes: List<String>) : List<HoverAction> {

//		val filteredActionIds = sdkDB.actionDao().filterIds(actionId,
//		                                                    actionRootCode,
//		                                                    actionIdList.toTypedArray(),
//		                                                    countryCodes.toTypedArray())
//		Timber.i("filter result size is {${filteredActionIds.size}}")
//		return sdkDB.actionDao().getActions(filteredActionIds.toTypedArray())
//	}

	suspend fun getNetworkNamesByCountryCodes(countryCodes: List<String>): List<String> {
		return sdkDB.actionDao().getNetworkNamesByCountryCodes(countryCodes.toTypedArray())
	}

	suspend fun getAllNetworkNames(): List<String> {
		return sdkDB.actionDao().allNetworkNames
	}

	private fun emptyToNull(values : Array<String>) : Array<String>?{
		return if(values.isEmpty()) null
		else values
	}
}