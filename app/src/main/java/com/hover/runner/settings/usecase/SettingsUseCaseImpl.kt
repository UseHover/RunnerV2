package com.hover.runner.settings.usecase

import com.hover.runner.settings.repo.SimRepoInterface

class SettingsUseCaseImpl(private val simInterface :SimRepoInterface) : SettingsUseCase {
    override suspend fun getPresentSimNames(): List<String> {
        return simInterface.getPresentSimNames()
    }

}