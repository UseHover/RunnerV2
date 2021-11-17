package com.hover.runner.settings.repo

import com.hover.sdk.sims.SimInfo

interface SimRepoInterface {
    suspend fun getPresentSims(): List<SimInfo>
}