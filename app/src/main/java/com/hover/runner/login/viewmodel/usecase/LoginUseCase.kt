package com.hover.runner.login.viewmodel.usecase

import com.hover.runner.utils.Resource

interface LoginUseCase {
    suspend fun login(email: String, password: String): Resource<Int>
    suspend fun validate(email: String, password: String): Resource<Int>
}