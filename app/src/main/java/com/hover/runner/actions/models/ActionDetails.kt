package com.hover.runner.actions.models

data class ActionDetails(
    var operators: String? = null,
    var parsers: String? = null,
    var transactionsNo: String? = null,
    var successNo: String? = null,
    var pendingNo: String? = null,
    var failedNo: String? = null,
    var streamlinedStepsModel: StreamlinedStepsModel? = null
)