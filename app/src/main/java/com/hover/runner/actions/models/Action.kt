package com.hover.runner.actions.models

import com.hover.runner.R
import com.hover.runner.actions.ActionStatusEnum
import com.hover.runner.utils.RunnerColor
import com.hover.sdk.transactions.Transaction
import com.hover.sdk.transactions.TransactionContract
import org.json.JSONArray

class Action(var id: String?, var title:String?,
             var rootCode: String?,
             var country: String?, var network_name: String?,
             var steps: JSONArray?, var statusEnum: ActionStatusEnum?, var jsonArrayToString: String? = "") {

        fun isPending() : Boolean {
            return statusEnum == ActionStatusEnum.PENDING
        }

        fun isFailed() : Boolean {
            return statusEnum == ActionStatusEnum.FAILED
        }

        fun isSuccessful() : Boolean {
            return statusEnum == ActionStatusEnum.SUCCEEDED
        }

        fun isNotYetRun() : Boolean {
            return statusEnum == ActionStatusEnum.NOT_YET_RUN
        }
    fun statusToString() : String {
        return Companion.statusToString(statusEnum)
    }

    fun getStatusColor() : Int{
   return when(statusEnum) {
        ActionStatusEnum.PENDING -> R.color.colorYellow
        ActionStatusEnum.FAILED -> R.color.colorRed
        ActionStatusEnum.SUCCEEDED -> R.color.colorGreen
        else -> R.color.colorPrimaryDark
    }
    }

    companion object {
        fun getStatus(string: String?) : ActionStatusEnum {
            return when(string) {
                Transaction.PENDING -> ActionStatusEnum.PENDING
                Transaction.FAILED -> ActionStatusEnum.FAILED
                Transaction.SUCCEEDED -> ActionStatusEnum.SUCCEEDED
                else -> ActionStatusEnum.NOT_YET_RUN
            }
        }

        fun statusToString(statusEnum: ActionStatusEnum?) : String {
            return when(statusEnum) {
                ActionStatusEnum.PENDING -> Transaction.PENDING
                ActionStatusEnum.FAILED -> Transaction.FAILED
                ActionStatusEnum.SUCCEEDED -> Transaction.SUCCEEDED
                else -> "NOT_YET_RUN";
            }
        }





    }




}

