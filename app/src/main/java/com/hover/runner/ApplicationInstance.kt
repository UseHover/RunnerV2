package com.hover.runner

import android.app.Application
import com.google.gson.GsonBuilder
import com.hover.runner.di.appModule
import com.hover.runner.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApplicationInstance : Application() {
    override fun onCreate() {
        super.onCreate()
        initDI()
        setRetrofit()
    }

    private fun initDI() {
        startKoin {
            androidContext(this@ApplicationInstance)
            modules(listOf(appModule, dataModule))
        }
    }

    companion object {
        lateinit var retrofit: Retrofit
    }

    private fun setRetrofit() {
        val gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(getString(R.string.api_url))
            .build()
    }
}