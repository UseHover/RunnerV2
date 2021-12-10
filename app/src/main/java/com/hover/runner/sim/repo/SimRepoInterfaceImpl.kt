package com.hover.runner.sim.repo

import com.hover.sdk.sims.SimInfo

class SimRepoInterfaceImpl(private val simRepo: SimRepo) : SimRepoInterface {
	override suspend fun getPresentSims(): List<SimInfo> {
		return simRepo.getPresentSims()
	}
}