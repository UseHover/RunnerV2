package com.hover.runner.actions

import com.hover.sdk.actions.HoverAction
import com.hover.sdk.transactions.Transaction
import org.json.JSONArray

class StyledAction(val action: HoverAction) {

	lateinit var status: String
	var steps: JSONArray? = null

	val name: String
		get() = action.name

	val public_id: String
		get() = action.public_id

	val root_code: String
		get() = action.root_code

	fun hasTransaction(status: String): Boolean {
		return when (status) {
			Transaction.PENDING -> true
			Transaction.FAILED -> true
			Transaction.SUCCEEDED -> true
			else -> false
		}
	}


}

