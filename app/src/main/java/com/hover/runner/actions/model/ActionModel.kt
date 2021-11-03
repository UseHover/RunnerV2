package com.hover.runner.actions.model

import com.hover.runner.actions.ActionStatusEnum
import org.json.JSONArray

data class ActionModel(
    var actionId: String?, var actionTitle:String?,
    var rootCode: String?, var jsonArrayToString: String?,
    var country: String?, var network_name: String?,
    var steps: JSONArray?, var actionStatusEnum: ActionStatusEnum?
)