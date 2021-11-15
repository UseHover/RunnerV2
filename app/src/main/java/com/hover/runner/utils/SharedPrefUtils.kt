package com.hover.runner.utils

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.hover.runner.R
import com.hover.sdk.api.HoverParameters

class SharedPrefUtils {
    companion object {

        private const val SHARED_PREFS = "_runner"
        private const val API_KEY_LABEL = "runner_apiKey"
        private const val DELAY = "delay"
        private const val TOKEN = "token"
        private const val ORG = "org_id"

        val ENV = "hoverEnv"
        val EMAIL = "hoverEmail"
        val PWD = "encryptedPwd"

        fun getSharedPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                getPackage(context).toString() + SHARED_PREFS, Context.MODE_PRIVATE
            )
        }

        fun saveString(key: String?, value: String?, c: Context) {
            val editor: SharedPreferences.Editor = getSharedPrefs(c).edit()
            editor.putString(key, value)
            editor.apply()
        }
        fun getSavedString(key: String?, c: Context): String? {
            return getSharedPrefs(c).getString(key, "")
        }

        fun saveInt(key: String?, value: Int, c: Context) {
            val editor: SharedPreferences.Editor = getSharedPrefs(c).edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun getSavedInt(key: String?, c: Context): Int {
            return getSharedPrefs(c).getInt(key, 0)
        }
        fun saveIntoStringSet(key: String, newValue: String, c: Context) {
            val editor: SharedPreferences.Editor = getSharedPrefs(c).edit()

            val data = getStringSet(key, c)
            data?.add(newValue)

            editor.putStringSet(key, data)
            editor.apply()
        }
        fun removeFromStringSet(key: String, value: String, c: Context) {
            val editor: SharedPreferences.Editor = getSharedPrefs(c).edit()
            val data = getStringSet(key, c)
            data?.remove(value)
            editor.putStringSet(key, data)
            editor.apply()
        }
        fun getStringSet(key: String, c: Context) : MutableSet<String>? {
            return getSharedPrefs(c).getStringSet(key, HashSet<String>())
        }

        fun setEnv(mode: Int, c: Context) {
            saveInt(ENV, mode, c)
        }

        fun chooseEnv(pos: Int, c: Context) {
            when (pos) {
                R.id.mode_normal -> setEnv(HoverParameters.PROD_ENV, c)
                R.id.mode_debug -> setEnv(HoverParameters.DEBUG_ENV, c)
                R.id.mode_noSim -> setEnv(HoverParameters.TEST_ENV, c)
            }
        }

        fun saveEmail(value: String, c: Context) {
            saveString(EMAIL, value, c)
        }

        fun getEmail(c: Context): String? {
            return getSavedString(EMAIL, c)
        }

        fun getPackage(c: Context): String? {
            return try {
                c.applicationContext.packageName
            } catch (e: NullPointerException) {
                "fail"
            }
        }

        fun saveToken(value: String?, c: Context) {
            saveString(TOKEN, value, c)
        }

        fun getToken(c: Context): String? {
            return getSavedString(TOKEN, c)
        }

        fun saveOrgId(value: Int, c: Context) {
            saveInt(ORG, value, c)
        }

        fun getOrgId(c: Context): Int {
            return getSavedInt(ORG, c)
        }

        fun saveApiKey(value: String?, c: Context) {
            saveString(API_KEY_LABEL, value, c)
        }

        fun getApiKey(c: Context): String? {
            return getSavedString(API_KEY_LABEL, c)
        }

        fun setDelay(value: Int, c: Context) {
            saveInt(DELAY, value, c)
        }

        fun getDelay(c: Context): Int {
            return getSavedInt(DELAY, c)
        }

        fun clearData(c: Context) {
            getSharedPrefs(c).edit().clear().apply()
            if (Build.VERSION.SDK_INT >= 19) (c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
        }
    }

}