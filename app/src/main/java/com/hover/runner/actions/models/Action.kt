package com.hover.runner.actions.models

import com.hover.runner.actions.ActionStatusEnum
import org.json.JSONArray

class Action
private constructor(var id: String?, var title:String?,
                    var rootCode: String?, var jsonArrayToString: String?,
                    var country: String?, var network_name: String?,
                    var steps: JSONArray?, var statusEnum: ActionStatusEnum?) {

        fun isPending() : Boolean {
            return statusEnum == ActionStatusEnum.PENDING
        }

        fun isFailed() : Boolean {
            return statusEnum == ActionStatusEnum.UNSUCCESSFUL
        }

        fun isSuccessful() : Boolean {
            return statusEnum == ActionStatusEnum.SUCCESS
        }

        fun isNotYetRun() : Boolean {
            return statusEnum == ActionStatusEnum.NOT_YET_RUN
        }

}

