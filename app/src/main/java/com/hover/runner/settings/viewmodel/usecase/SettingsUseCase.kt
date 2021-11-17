package com.hover.runner.settings.viewmodel.usecase

interface SettingsUseCase {
    suspend fun getPresentSimNames(): List<String>
}