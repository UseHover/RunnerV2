package com.hover.runner.actions.models

import com.hover.runner.transactions.model.RunnerTransaction
import com.hover.sdk.transactions.Transaction

data class ActionDetails(
    var operators: String? = null,
    var parsers: String? = null,
    val transactionsNo: String? = null,
    val successNo: String? = null,
    val pendingNo: String? = null,
    val failedNo: String? = null,
    var streamlinedSteps: StreamlinedSteps? = null
) {
    companion object {
        fun init(transactionList: List<RunnerTransaction>): ActionDetails {
            var totalTransaction = "0"
            var successNo = 0
            var pendingNo = 0
            var failedNo = 0
            if (transactionList.isNotEmpty()) {
                totalTransaction = transactionList.size.toString()
                transactionList.forEach {
                    if (it.status == Transaction.SUCCEEDED) successNo += 1
                    else if (it.status == Transaction.PENDING) pendingNo += 1
                    else failedNo += 1
                }
            }
            return ActionDetails(
                null,
                null,
                totalTransaction,
                successNo.toString(),
                pendingNo.toString(),
                failedNo.toString()
            )

        }
    }
}