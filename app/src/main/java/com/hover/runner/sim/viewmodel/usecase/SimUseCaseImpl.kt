package com.hover.runner.sim.viewmodel.usecase

import com.hover.runner.sim.repo.SimRepoInterface

class SimUseCaseImpl(private val simInterface: SimRepoInterface) : SimUseCase {
	override suspend fun getPresentSimNames(): List<String> {
		val presentSims = simInterface.getPresentSims()
		val simNames = mutableListOf<String>()
		presentSims.forEach { simNames += it.operatorName }
		return simNames
	}

	override suspend fun getSimCountryCodes(): List<String> {
		val presentSims = simInterface.getPresentSims()
		val countryCodes = mutableListOf<String>()
		presentSims.forEach { countryCodes += it.countryIso }
		return countryCodes
	}

}