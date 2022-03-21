package com.hover.runner.login.viewmodel.usecase

import android.content.Context
import com.hover.runner.R
import com.hover.runner.login.Apis
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
		}
		else {
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
		Timber.e("cacheOrg is %s", token.org_id)
		Timber.e("cacheToken is %s", token.auth_token)
		Timber.e("cacheAPIKey is %s", token.api_key)

		SharedPrefUtils.saveOrgId(token.org_id, context)
		SharedPrefUtils.saveToken(token.auth_token, context)
		SharedPrefUtils.saveApiKey(token.api_key, context)
	}
}