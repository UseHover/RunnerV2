package com.hover.runner.sim.repo

import com.hover.sdk.sims.SimInfo

interface SimRepoInterface {
	suspend fun getPresentSims(): List<SimInfo>
}