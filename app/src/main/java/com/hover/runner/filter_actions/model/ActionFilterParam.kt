package com.hover.runner.filter_actions.model

import android.content.Context
import com.hover.runner.utils.DateUtils
import com.hover.runner.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

data class ActionFilterParam(
    var actionId: String = "",
    var actionRootCode: String = "",
    var actionIdList : List<String> = ArrayList(),
    var countryNameList :  List<String> = ArrayList(),
    var networkNameList : List<String> = ArrayList(),
    var categoryList : List<String> = ArrayList(),
    var startDate : Long = 0,
    var endDate : Long = 0,
    var transactionSuccessful : String  = "",
    var transactionPending : String = "",
    var transactionFailed : String = "",
    var hasNoTransaction : Boolean = false,
    var hasParser : Boolean = false,
    var onlyWithSimPresent : Boolean = false
) {

    fun isDefault() : Boolean {
        return this == getDefault()
    }

    fun getDateRangeValue(context: Context) : String {
        return if(endDate > 0) String.format(Locale.getDefault(), "%s - %s", DateUtils.formatDateV2(startDate), DateUtils.formatDateV3(endDate))
        else String.format(Locale.getDefault(), "From %s - %s", "<account creation>", DateUtils.formatDateV3(Date().time))
    }

    fun getActionIdsAsString() : String {
        return Utils.toString(actionIdList)
    }

    fun getCountryListAsString() : String {
        return Utils.toString(countryNameList)
    }

    fun getNetworkNamesAsString() : String {
        return Utils.toString(networkNameList)
    }

    fun getCategoryListAsString() : String {
        return Utils.toString(categoryList)
    }

    fun isTransactionSuccessfulIncluded() : Boolean {
        return transactionSuccessful.isNotEmpty()
    }
    fun isTransactionPendingIncluded() : Boolean{
        return transactionPending.isNotEmpty()
    }
    fun isTransactionFailedIncluded() : Boolean {
        return transactionFailed.isNotEmpty()
    }


    companion object {
        fun getDefault() : ActionFilterParam {
            return ActionFilterParam()
        }
    }
}