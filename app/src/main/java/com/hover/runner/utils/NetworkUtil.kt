package com.hover.runner.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import java.lang.Exception

class NetworkUtil(private val context: Context) {
    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
                            NetworkCapabilities.TRANSPORT_WIFI
                        )
                }
            } else {
                try {
                    val activeNetworkInfo = connectivityManager.activeNetworkInfo
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) { return true }
                } catch (e: Exception) {
                    return false
                }
            }
            return false
        }
}