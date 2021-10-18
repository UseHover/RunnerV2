package com.hover.runner.api

import com.hover.runner.ApplicationInstance
import com.hover.runner.login.endpoint.LoginEndpoint
import com.hover.runner.login.endpoint.TokenModel
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException

class Apis {
    companion object {
        fun login(email : String,  password: String) : TokenModel? {
            val retrofit = ApplicationInstance.retrofit
            val retrofitToken = retrofit.create(LoginEndpoint::class.java)
            val emailBody = RequestBody.create(MediaType.parse("text/plain"), email)
            val passwordBody = RequestBody.create(MediaType.parse("text/plain"), password)
            val callerToken: Call<TokenModel> = retrofitToken.getTokenFromHover(emailBody, passwordBody)
            val tokenModel = callerToken.execute()
            return try {
                if (tokenModel.code() == 200 && tokenModel.body() != null) tokenModel.body()
                else null
            }catch (e : IOException) {
                Timber.e(e)
                null
            }

        }
    }

}