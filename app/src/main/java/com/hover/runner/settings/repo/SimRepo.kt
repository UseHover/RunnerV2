package com.hover.runner.settings.repo

import android.content.Context
import com.hover.sdk.api.Hover
import com.hover.sdk.sims.SimInfo

class SimRepo(private val context: Context) {
    suspend fun getPresentSims() : List<SimInfo> {
        return Hover.getPresentSims(context)
    }
}