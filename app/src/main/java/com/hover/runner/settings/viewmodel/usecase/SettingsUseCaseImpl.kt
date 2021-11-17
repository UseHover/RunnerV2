package com.hover.runner.settings.viewmodel.usecase

import com.hover.runner.settings.repo.SimRepoInterface

class SettingsUseCaseImpl(private val simInterface: SimRepoInterface) : SettingsUseCase {
    override suspend fun getPresentSimNames(): List<String> {
        val presentSims  = simInterface.getPresentSims()
        val simNames = mutableListOf<String>()
        presentSims.forEach { simNames += it.operatorName }
        return simNames
    }

}