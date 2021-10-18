package com.hover.runner.login.usecase

import android.content.Context
import com.hover.runner.R
import com.hover.runner.api.Apis
import com.hover.runner.login.endpoint.TokenModel
import com.hover.runner.utils.NetworkUtil
import com.hover.runner.utils.Resource
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.Utils

class LoginUseCaseImpl(private val context: Context) : LoginUseCase {
    override suspend fun login(email: String, password: String): Resource<Int> {
        val token : TokenModel? = Apis.login(email, password)
            return if(token !=null) {
                cacheToken(token)
                Resource.Success(R.string.sign_in_success)
            } else Resource.ErrorWithRes(R.string.sign_in_err)

    }

    override suspend fun validate(email: String, password: String): Resource<Int> {
        if(!Utils.validateEmail(email)) return Resource.ErrorWithRes(R.string.INVALID_EMAIL)
        else if(!Utils.validatePassword(password)) return Resource.ErrorWithRes(R.string.INVALID_PASSWORD)
        else if(!NetworkUtil(context).isNetworkAvailable) return Resource.ErrorWithRes(R.string.NO_NETWORK)
        else return Resource.Success(0)
    }

    private fun cacheToken(token: TokenModel) {
        SharedPrefUtils.saveOrgId(token.orgId, context)
        SharedPrefUtils.saveToken(token.auth_token, context)
        SharedPrefUtils.saveApiKey(token.apiKey, context)
    }
}