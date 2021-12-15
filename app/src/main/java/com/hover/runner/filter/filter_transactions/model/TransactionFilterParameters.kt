package com.hover.runner.filter.filter_transactions.model

import android.content.Context
import com.hover.runner.action.repo.ActionIdsInANetworkRepo
import com.hover.runner.utils.DateUtils
import com.hover.runner.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

data class TransactionFilterParameters(
	var actionIdList: List<String> = ArrayList(),
	var countryCodeList: List<String> = ArrayList(),
	var networkNameList: List<String> = ArrayList(),
	var startDate: Long = 0,
	var endDate: Long = 0,
	var successful: String = "",
	var pending: String = "",
	var failed: String = "",
) {
	fun isDefault(): Boolean {
		return this == getDefault()
	}

	fun getDateRangeValue(): String {
		return if (endDate > 0) String.format(Locale.getDefault(),
		                                      "%s - %s",
		                                      DateUtils.formatDateV2(startDate),
		                                      DateUtils.formatDateV3(endDate))
		else String.format(Locale.getDefault(),
		                   "From %s - %s",
		                   "<account creation>",
		                   DateUtils.formatDateV3(Date().time))
	}

	fun shouldFilterByTransactionStatus() : Boolean{
		return successful.isNotEmpty() || pending.isNotEmpty() || failed.isNotEmpty()
	}

	fun isActionRelatedParamSelected() : Boolean {
		return actionIdList.isNotEmpty() && countryCodeList.isNotEmpty() && networkNameList.isNotEmpty()
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


	fun getCountryListAsString(): String {
		val toFullCountryNames : List<String> = countryCodeList.map { Locale("EN", it).displayCountry }
		return Utils.toString(toFullCountryNames)
	}

	fun getNetworkNamesAsString(): String {
		return Utils.toString(networkNameList)
	}

	fun isTransactionSuccessfulIncluded(): Boolean {
		return successful.isNotEmpty()
	}

	fun isTransactionPendingIncluded(): Boolean {
		return pending.isNotEmpty()
	}

	fun isTransactionFailedIncluded(): Boolean {
		return failed.isNotEmpty()
	}


	companion object {
		fun getDefault(): TransactionFilterParameters {
			return TransactionFilterParameters()
		}
	}

}