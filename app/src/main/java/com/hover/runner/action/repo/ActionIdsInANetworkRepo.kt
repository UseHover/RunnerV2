package com.hover.runner.action.repo

import android.content.Context
import com.hover.runner.action.models.Action
import com.hover.runner.utils.SharedPrefUtils

object ActionIdsInANetworkRepo {

	private fun save(networkName: String, actionId: String, context: Context) {
		SharedPrefUtils.saveIntoStringSet(networkName, actionId, context)
	}

	fun cache(actions: List<Action>, context: Context) {
		actions.forEach {
			val networkNames = it.network_name?.split(",") ?: emptyList()

			networkNames.forEach{networkName->
				save(networkName, it.id, context)
			}
		}
	}

	fun getIds(networkName: String, context: Context) : Array<String>{
		val ids = SharedPrefUtils.getStringSet(networkName, context)
		return ids?.toTypedArray() ?: emptyArray()
	}
}