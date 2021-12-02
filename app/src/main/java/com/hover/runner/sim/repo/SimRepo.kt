package com.hover.runner.sim.repo

import com.hover.sdk.database.HoverRoomDatabase
import com.hover.sdk.sims.SimInfo

class SimRepo(private val sdk: HoverRoomDatabase) {
    suspend fun getPresentSims(): List<SimInfo> {
        return sdk.simDao().present
    }
}