package com.hover.runner.filter.filter_actions.model

import android.content.Context
import com.hover.runner.action.repo.ActionIdsInANetworkRepo
import com.hover.runner.utils.DateUtils
import com.hover.runner.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

data class ActionFilterParameters(var actionId: String = "",
                                  var actionRootCode: String = "",
                                  var actionIdList: List<String> = ArrayList(),
                                  var countryCodeList: List<String> = ArrayList(),
                                  var networkNameList: List<String> = ArrayList(),
                                  var categoryList: List<String> = ArrayList(),
                                  var startDate: Long = 0,
                                  var endDate: Long = 0,
                                  var transactionSuccessful: String = "",
                                  var transactionPending: String = "",
                                  var transactionFailed: String = "",
                                  var includeActionsWithNoTransaction: Boolean = false,
                                  var hasParser: Boolean = false,
                                  var onlyWithSimPresent: Boolean = false) {

	fun isDefault(): Boolean {
		return this == getDefault()
	}

	fun getDateRangeValue(): String {
		return if (endDate > 0) String.format(Locale.getDefault(), "%s - %s",
		                                      DateUtils.formatDateV2(startDate),
		                                      DateUtils.formatDateV3(endDate))
		else String.format(Locale.getDefault(),
		                   "From %s - %s",
		                   "<account creation>",
		                   DateUtils.formatDateV3(Date().time))
	}

	fun getActionIdOrRootCode(): String {
		return if (actionId.isNotEmpty()) actionId else actionRootCode
	}

	fun getActionIdsAsString(): String {
		return Utils.toString(actionIdList)
	}
	fun getTotalActionIds(context: Context) : Array<String> {
		val totalList: List<String> =
			if (networkNameList.isNotEmpty()) actionIdList + ActionIdsInANetworkRepo.getIds(networkNameList, context)
			else actionIdList
		return totalList.toTypedArray()
	}

	fun shouldFilterByTransactionStatus() : Boolean{
		return transactionSuccessful.isNotEmpty() || transactionPending.isNotEmpty() || transactionFailed.isNotEmpty()
	}

	fun getCountryListAsString(): String {
		val toFullCountryNames : List<String> = countryCodeList.map { Locale("EN", it).displayCountry }
		return Utils.toString(toFullCountryNames)
	}

	fun getNetworkNamesAsString(): String {
		return Utils.toString(networkNameList)
	}

	fun getCategoryListAsString(): String {
		return Utils.toString(categoryList)
	}

	fun isTransactionSuccessfulIncluded(): Boolean {
		return transactionSuccessful.isNotEmpty()
	}

	fun isTransactionPendingIncluded(): Boolean {
		return transactionPending.isNotEmpty()
	}

	fun isTransactionFailedIncluded(): Boolean {
		return transactionFailed.isNotEmpty()
	}


	companion object {
		fun getDefault(): ActionFilterParameters {
			return ActionFilterParameters()
		}
	}
}