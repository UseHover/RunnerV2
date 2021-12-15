package com.hover.runner.filter.filter_transactions.model

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
	fun getActionIdsAsString(): String {
		return Utils.toString(actionIdList)
	}

	fun getCountryListAsString(): String {
		return Utils.toString(countryCodeList)
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