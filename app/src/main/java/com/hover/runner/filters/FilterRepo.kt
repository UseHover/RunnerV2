package com.hover.runner.filters

import androidx.sqlite.db.SimpleSQLiteQuery
import com.hover.sdk.database.HoverRoomDatabase
import timber.log.Timber

abstract class FilterRepo(private val sdkDB: HoverRoomDatabase) {
	abstract fun getTable(): String

	abstract fun search(sqlWhere: SimpleSQLiteQuery): List<Any>

	fun getAllTags(): List<String> {
		return sdkDB.actionDao().allTags.toString()
			.replace("[", "").replace("]", "")
			.replace("\"", "").replace(" ", "")
			.split(",").distinct().filter { it.isNotEmpty() }
	}

	fun generateSQLStatement(search: String?, tagList: List<String>?): SimpleSQLiteQuery {
		var fString = getTable()

		if (generateSearchString(search).isNotEmpty() || generateTagString(tagList).isNotEmpty())
			fString += " WHERE "

		fString += generateSearchString(search)
		fString += generateTagString(tagList)

		Timber.e("Searching %s", fString)
		return SimpleSQLiteQuery(fString)
	}

	abstract fun generateSearchString(search: String?): String

	private fun generateTagString(tagList: List<String>?): String {
		if (tagList?.size == getAllTags().size || getAllTags().isEmpty())
			return ""
		else if (tagList.isNullOrEmpty())
			return "(tags_list IS NULL OR tags_list = '')"

		var sqlStr = "("
		for ((i, tag) in tagList.withIndex()) {
			if (i > 0) sqlStr += " OR "
			val t = "%$tag%"
			sqlStr += "tags_list LIKE '$t'"
		}
		sqlStr += ")"
		return sqlStr
	}

	fun getAllCountryCodes(): List<String> {
		return sdkDB.actionDao().allCountryCodes
	}

	fun getNetworkNamesByCountry(countryCodes: List<String>): List<String> {
		return sdkDB.actionDao().getNetworkNamesByCountryCodes(countryCodes.toTypedArray())
	}

	fun getAllNetworkNames(): List<String> {
		return sdkDB.actionDao().allNetworkNames
	}
}