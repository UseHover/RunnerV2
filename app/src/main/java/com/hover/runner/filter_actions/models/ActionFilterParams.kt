package com.hover.runner.filter_actions.models

data class ActionFilterParams(
    var actionIdList : List<String> = ArrayList(),
    var countryCodeList :  List<String> = ArrayList(),
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

    companion object {
        fun getDefault() : ActionFilterParams {
            return ActionFilterParams()
        }
    }
}