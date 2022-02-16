package com.hover.runner.actions

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.database.HoverRoomDatabase
import timber.log.Timber

class ActionRepo(private val sdkDB: HoverRoomDatabase) {
	fun getAll(): LiveData<List<HoverAction>> {
		return sdkDB.actionDao().allLive
	}

	fun load(id: String): HoverAction {
		return sdkDB.actionDao().getAction(id)
	}

	fun getHoverActions(ids: Array<String>): List<HoverAction> {
		return sdkDB.actionDao().getActions(ids)
	}

	fun search(sqlWhere: SimpleSQLiteQuery): List<HoverAction> {
		return sdkDB.actionDao().search(sqlWhere)
	}

	fun getAllTags(): List<String> {
		return sdkDB.actionDao().allTags.toString()
			.replace("[", "").replace("]", "")
			.replace("\"", "").replace(" ", "")
			.split(",").distinct().filter { it.isNotEmpty() }
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

	fun generateSQLStatement(search: String?, tagList: List<String>?): SimpleSQLiteQuery {
		var fString = SQL_SELECT

		if (!generateSearchString(search).isEmpty() || !generateTagString(tagList).isEmpty())
			fString += " WHERE "

		fString += generateSearchString(search)
		fString += generateTagString(tagList)

		Timber.e("Searching %s", fString)
		return SimpleSQLiteQuery(fString)
	}

	private fun generateSearchString(search: String?): String {
		if (search.isNullOrEmpty())
			return ""
		val s = "%$search%"
		return "(server_id LIKE '$s' OR name LIKE '$s')"
	}

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
}