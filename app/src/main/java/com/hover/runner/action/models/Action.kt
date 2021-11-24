package com.hover.runner.action.models

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.hover.runner.BuildConfig
import com.hover.runner.transaction.TransactionStatus
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.utils.SharedPrefUtils
import com.hover.sdk.actions.HoverAction
import org.json.JSONArray

data class Action(
    val id: String, var title: String?,
    val rootCode: String,
    var country: String?, var network_name: String?,
    var steps: JSONArray?, var status: String, var isSkipped: Boolean = false,
    var jsonArrayToString: String? = ""
) : TransactionStatus() {


    fun getStatusColor(): Int {
        return getColor(status)
    }

    fun getLayoutColor(): Int {
        return getToolBarColor(status)
    }

    fun getStatusDrawable(): Int {
        return getDrawable(status)
    }

    @SuppressLint("LogNotTimber")
    fun hasAllVariablesFilled(c: Context): Boolean {
        val expectedVariableSize: Int =
            StreamlinedSteps.get(rootCode, steps!!).stepVariableLabel.size
        val variables: Map<String, String> = ActionVariablesCache.get(c, id).actionMap

        var filledSize = 0
        for (value in variables.values) {
            if (!TextUtils.isEmpty(value.replace(" ", ""))) filledSize += 1
        }

        if (!BuildConfig.FLAVOR.contains("pro")) filledSize += 1
        Log.d("VAR_SIZE", "expected size: $expectedVariableSize, while filled size is: $filledSize")
        return expectedVariableSize == filledSize
    }

    fun saveAsSkipped(context: Context) {
        SharedPrefUtils.saveIntoStringSet(skippedKey, id, context)
    }

    fun removeFromSkipped(context: Context) {
        SharedPrefUtils.removeFromStringSet(skippedKey, id, context)
    }

    companion object {
        const val skippedKey = "action_skip"

        private fun isSkipped(actionId: String, context: Context): Boolean {
            val skippedList = SharedPrefUtils.getStringSet(skippedKey, context)
            return skippedList!!.contains(actionId)
        }

        fun get(act: HoverAction, lastTransaction: RunnerTransaction?, context: Context): Action {
            return Action(
                act.public_id,
                act.name,
                act.root_code,
                act.country_alpha2,
                act.network_name,
                act.custom_steps,
                lastTransaction?.status ?: "",
                isSkipped(act.public_id, context)
            )
        }
    }
}

