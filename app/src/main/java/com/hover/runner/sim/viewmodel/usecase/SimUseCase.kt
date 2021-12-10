package com.hover.runner.sim.viewmodel.usecase

interface SimUseCase {
	suspend fun getPresentSimNames(): List<String>
	suspend fun getSimCountryCodes(): List<String>
}