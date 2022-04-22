package com.hover.runner.actions

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.core.util.Pair
import com.hover.runner.filters.FilterRepo
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.database.HoverRoomDatabase

class ActionRepo(private val sdkDB: HoverRoomDatabase): FilterRepo(sdkDB) {
	override fun getTable(): String { return "SELECT * FROM hover_actions" }

	fun getAll(): LiveData<List<HoverAction>> {
		return sdkDB.actionDao().allLive
	}

	fun load(id: String): HoverAction {
		return sdkDB.actionDao().getAction(id)
	}

	fun getHoverActions(ids: Array<String>): List<HoverAction> {
		return sdkDB.actionDao().getActions(ids)
	}

	override fun search(sqlWhere: SimpleSQLiteQuery): List<HoverAction> {
		return sdkDB.actionDao().search(sqlWhere)
	}

	override fun generateSearchString(search: String?): String {
		if (search.isNullOrEmpty())
			return ""
		val s = "%$search%"
		return "(server_id LIKE '$s' OR name LIKE '$s')"
	}

	override fun generateDateString(dateRange: Pair<Long, Long>?): String {
		return ""
	}
}