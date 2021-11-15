package com.hover.runner.settings.repo

import androidx.lifecycle.LiveData
import com.hover.sdk.database.HoverRoomDatabase
import com.hover.sdk.sims.SimInfo
import com.hover.sdk.sims.SimInfoDao

class SimRepo(private val sdk: HoverRoomDatabase) {
    suspend fun getPresentSims() : List<SimInfo> {
        return sdk.simDao().present
    }
}