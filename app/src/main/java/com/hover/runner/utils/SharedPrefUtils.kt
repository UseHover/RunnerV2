package com.hover.runner.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.hover.runner.R
import com.hover.sdk.api.HoverParameters
import timber.log.Timber

object SharedPrefUtils {

	private const val SHARED_PREFS = "_runner"
	private const val API_KEY_LABEL = "runner_apiKey"
	const val DELAY = "delay"
	private const val TOKEN = "token"
	private const val ORG = "org_id"

	val ENV = "hoverEnv"
	val EMAIL = "hoverEmail"

	fun getSharedPrefs(context: Context): SharedPreferences {
		return context.getSharedPreferences(getPackage(context).toString() + SHARED_PREFS,
		                                    Context.MODE_PRIVATE)
	}

	private fun saveString(key: String?, value: String?, c: Context) {
		val editor: SharedPreferences.Editor = getSharedPrefs(c).edit()
		editor.putString(key, value)
		editor.apply()
	}

	fun getSavedString(key: String?, c: Context): String {
		return getSharedPrefs(c).getString(key, "")!!
	}

	fun saveBool(key: String, value: Boolean, c: Context) {
		val editor: SharedPreferences.Editor = getSharedPrefs(c).edit()
		editor.putBoolean(key, value)
		editor.commit()
	}

	fun getSavedBool(key: String?, c: Context): Boolean {
		return getSharedPrefs(c).getBoolean(key, false)
	}

	fun saveVar(action_id: String, key: String?, value: String, c: Context) {
		val editor: SharedPreferences.Editor = getSharedPrefs(c).edit()
		editor.putString("$action_id-variable-$key", value)
		editor.commit()
	}

	fun getVarValue(action_id: String, key: String?, c: Context): String {
		if (key == null) { return "" }
		return getSharedPrefs(c).getString("$action_id-variable-$key", "")!!
	}

	fun saveInt(key: String?, value: Int, c: Context) {
		val editor: SharedPreferences.Editor = getSharedPrefs(c).edit()
		editor.putInt(key, value)
		editor.apply()
	}

	fun getSavedInt(key: String?, c: Context): Int {
		return getSharedPrefs(c).getInt(key, 0)
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
		val editor: SharedPreferences.Editor = getSharedPrefs(c).edit()
		editor.putInt(DELAY, value)
		editor.apply()
	}

	fun getDelay(c: Context): Int {
		return getSharedPrefs(c).getInt(DELAY, 5)
	}

	fun clearData(c: Context) {
		getSharedPrefs(c).edit().clear().apply()
		if (Build.VERSION.SDK_INT >= 19) (c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
	}
}