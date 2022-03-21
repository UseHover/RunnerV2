package com.hover.runner.login.endpoint

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LoginEndpoint {
	@Multipart
	@POST("authenticate")
	fun getTokenFromHover(@Part("email") email: RequestBody?,
	                      @Part("password") password: RequestBody?): Call<Token>
}