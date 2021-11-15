package com.hover.runner.settings.repo

interface SimRepoInterface {
    suspend fun getPresentSimNames() : List<String>
}