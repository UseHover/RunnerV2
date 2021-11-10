package com.hover.runner.actions.models

import com.hover.sdk.actions.HoverAction

data class ActionDetails(
    var operators: String? = null,
    var parsers: String? = null,
    var transactionsNo: String? = null,
    var successNo: String? = null,
    var pendingNo: String? = null,
    var failedNo: String? = null,
    var streamlinedSteps: StreamlinedSteps? = null
) {
    companion object {
        fun get(hoverAction: HoverAction) {

        }
    }
}