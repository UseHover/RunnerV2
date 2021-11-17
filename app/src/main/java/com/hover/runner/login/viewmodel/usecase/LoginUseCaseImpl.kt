package com.hover.runner.login.viewmodel.usecase

import android.content.Context
import com.hover.runner.R
import com.hover.runner.api.Apis
import com.hover.runner.login.endpoint.Token
import com.hover.runner.utils.NetworkUtil
import com.hover.runner.utils.Resource
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.Utils
import timber.log.Timber

class LoginUseCaseImpl(private val context: Context) : LoginUseCase {
    override suspend fun login(email: String, password: String): Resource<Int> {
        val token: Token? = Apis.login(email, password)
        return if (token != null) {
            Timber.i("Login is successful")
            cacheToken(token)
            Resource.Success(R.string.sign_in_success)
        } else {
            Timber.i("Login failed")
            Resource.ErrorWithRes(R.string.sign_in_err)
        }

    }

    override suspend fun validate(email: String, password: String): Resource<Int> {
        if (!Utils.validateEmail(email)) return Resource.ErrorWithRes(R.string.INVALID_EMAIL)
        else if (!Utils.validatePassword(password)) return Resource.ErrorWithRes(R.string.INVALID_PASSWORD)
        else if (!NetworkUtil(context).isNetworkAvailable) return Resource.ErrorWithRes(R.string.NO_NETWORK)
        else return Resource.Success(0)
    }

    private fun cacheToken(token: Token) {
        Timber.i("cacheOrg is ${token.orgId}")
        Timber.i("cacheToken is ${token.auth_token}")
        Timber.i("cacheAPIKey is ${token.apiKey}")

        //SharedPrefUtils.saveOrgId(801733, context)
        //SharedPrefUtils.saveToken(token.auth_token, context)
        //SharedPrefUtils.saveApiKey("cd8c5a3a29a52f534f5befad8779fcc0", context)

        SharedPrefUtils.saveOrgId(token.orgId, context)
        SharedPrefUtils.saveToken(token.auth_token, context)
        SharedPrefUtils.saveApiKey(token.apiKey, context)
    }
}