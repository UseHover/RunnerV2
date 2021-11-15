package com.hover.runner

import android.app.Application
import com.google.gson.GsonBuilder
import com.hover.runner.di.appModule
import com.hover.runner.di.dataModule
import com.hover.runner.utils.fonts.FontReplacer
import com.hover.runner.utils.fonts.Replacer
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApplicationInstance : Application() {
    override fun onCreate() {
        super.onCreate()
        initDI()
        setRetrofit()
        setupFonts()
    }

    private fun setupFonts() {
        val replacer = FontReplacer.Build(applicationContext)
        replacer.setDefaultFont("Gibson-Regular.otf")
        replacer.setBoldFont("Gibson-Bold.otf")
        replacer.setItalicFont("Gibson-SemiBoldItalic.otf")
        replacer.setThinFont("Gibson-Light.otf")
        replacer.applyFont()
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