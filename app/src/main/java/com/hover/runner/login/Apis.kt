package com.hover.runner.login

import com.hover.runner.ApplicationInstance
import com.hover.runner.login.endpoint.LoginEndpoint
import com.hover.runner.login.endpoint.Token
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import timber.log.Timber
import java.io.IOException

class Apis {
	companion object {
		fun login(email: String, password: String): Token? {
			val retrofit = ApplicationInstance.retrofit
			val retrofitToken = retrofit.create(LoginEndpoint::class.java)
			val emailBody = RequestBody.create(MediaType.parse("text/plain"), email)
			val passwordBody = RequestBody.create(MediaType.parse("text/plain"), password)
			val callerToken: Call<Token> = retrofitToken.getTokenFromHover(emailBody, passwordBody)
			val tokenModel = callerToken.execute()
			return try {
				Timber.e(tokenModel.body().toString())
				if (tokenModel.code() == 200 && tokenModel.body() != null) tokenModel.body() else null
			} catch (e: IOException) {
				Timber.e(e)
				null
			}

		}
	}

}