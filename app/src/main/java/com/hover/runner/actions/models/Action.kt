package com.hover.runner.actions.models

import com.hover.runner.transactions.TransactionStatus
import com.hover.runner.transactions.model.RunnerTransaction
import com.hover.sdk.actions.HoverAction
import org.json.JSONArray

class Action(var id: String?, var title:String?,
             var rootCode: String?,
             var country: String?, var network_name: String?,
             var steps: JSONArray?, var status: String?, var jsonArrayToString: String? = "") : TransactionStatus() {

    fun getStatusColor() : Int{
        return getColor(status)
    }

    fun getStatusDrawable() : Int {
            return getDrawable(status)
    }

    companion object {
        fun get(act: HoverAction, lastTransaction: RunnerTransaction?) : Action {
            return Action(
                act.public_id, act.from_institution_name,
                act.root_code, act.country_alpha2,
                act.network_name, act.custom_steps, lastTransaction?.status
            )
        }
    }
}

