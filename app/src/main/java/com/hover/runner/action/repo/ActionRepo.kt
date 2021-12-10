package com.hover.runner.action.repo

import com.hover.sdk.actions.HoverAction
import com.hover.sdk.database.HoverRoomDatabase

class ActionRepo(private val sdkDB: HoverRoomDatabase) {
	suspend fun getAllActionsFromHover(): List<HoverAction> {
		return sdkDB.actionDao().all
	}

	suspend fun getHoverAction(id: String): HoverAction {
		return sdkDB.actionDao().getAction(id)
	}

	fun getAllActionsCountryCodes(): List<String> {
		return ArrayList()
	}

	suspend fun filterHoverAction(): List<HoverAction> {
		return ArrayList()
	}

	suspend fun getNetworkNamesByCountryCodes(countryCodes: List<String>): List<String> {
		return ArrayList()
	}

	suspend fun getAllNetworkNames(): List<String> {
		return ArrayList()
	}
}