package com.hover.runner.settings.usecase

interface SettingsUseCase {
    suspend fun getPresentSimNames(): List<String>
}