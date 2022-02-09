package com.hover.runner.settings

import com.hover.sdk.database.HoverRoomDatabase
import com.hover.sdk.sims.SimInfoDao

class SimsRepo(private val sdk: HoverRoomDatabase) {
	val simDao: SimInfoDao = sdk.simDao()
}