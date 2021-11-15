package com.hover.runner.settings.repo

class SimRepoInterfaceImpl(private val simRepo: SimRepo) : SimRepoInterface {
    override suspend fun getPresentSimNames(): List<String> {
       val simInfo = simRepo.getPresentSims()
        val result = mutableListOf<String>()
        simInfo.forEach { result += it.operatorName }
        return result
    }
}