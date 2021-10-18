package com.hover.runner.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.hover.runner.R
import com.hover.sdk.api.HoverParameters.*
import org.json.JSONArray
import org.json.JSONException
import java.lang.Exception
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class Utils {
    companion object {
        val ENV = "hoverEnv"
        val EMAIL = "hoverEmail"
        val PWD = "encryptedPwd"
        private val API_KEY_LABEL = "apiKey"
        private val DELAY = "delay"
        private val TOKEN = "token"
        private val ORG = "org_id"

        val TESTER_VERSION = "1.0 (1)"
        private val HOVER_TRANSAC_FAILED = "failed"
        val HOVER_TRANSAC_PENDING = "pending"
        val HOVER_TRANSAC_SUCCEEDED = "succeeded"
        private val SHARED_PREFS = "_runner"
        private val PRO_VARIANT = "pro"

        fun hasPermissions(context: Context, permissions: Array<String?>?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission!!
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }

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

        fun saveInt(key: String?, value: Int, c: Context) {
            val editor: SharedPreferences.Editor = getSharedPrefs(c).edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun getSavedString(key: String?, c: Context): String? {
            return getSharedPrefs(c).getString(key, "")
        }

        fun getSavedInt(key: String?, c: Context): Int {
            return getSharedPrefs(c).getInt(key, 0)
        }


        fun validateEmail(string: String): Boolean {
            val patterns =
                Pattern.compile("(?:[A-Za-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
            return patterns.matcher(string).matches()
        }

        fun validatePassword(string: String?): Boolean {
            return if (string == null) false else string.length < 40 && string.length > 4 && !string.contains(
                " "
            )
        }

        @Throws(JSONException::class)
        fun convertNormalJSONArrayToStringArray(arr: JSONArray?): Array<String?>? {
            if (arr == null) return arrayOf()
            val list = arrayOfNulls<String>(arr.length())
            for (i in 0 until arr.length()) {
                list[i] = arr.getString(i)
            }
            return list
        }

        fun formatDate(timestamp: Long): String? {
            val pattern = "HH:mm:ss (z) MMM dd, yyyy"
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            return simpleDateFormat.format(timestamp)
        }

        fun formatDateV2(timestamp: Long): String? {
            val pattern = "MMM dd"
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            return simpleDateFormat.format(timestamp)
        }

        fun formatDateV3(timestamp: Long): String? {
            val pattern = "MMM dd, yyyy"
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            return simpleDateFormat.format(timestamp)
        }

        fun nonNullDateRange(value: Any?): Any? {
            return value ?: 0
        }

        @SuppressLint("HardwareIds", "MissingPermission")
        fun getDeviceId(c: Context): String? {
            try {
                if (hasPermissions(
                        c,
                        arrayOf(
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CALL_PHONE
                        )
                    )
                ) {
                    var id: String? = null
                    if (Build.VERSION.SDK_INT < 29) {
                        try {
                            id =
                                (c.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
                        } catch (ignored: Exception) {
                        }
                    }
                    if (id == null) {
                        id =
                            Settings.Secure.getString(c.contentResolver, Settings.Secure.ANDROID_ID)
                    }
                    return id
                }
            } catch (ignored: SecurityException) {
            }
            return c.getString(R.string.hsdk_unknown_device_id)
        }


        fun envValueToString(env: Int): String? {
            var string = ""
            string = when (env) {
                0 -> "Normal"
                1 -> "Debug"
                else -> "No-SIM"
            }
            return string
        }

        fun nullToString(value: Any?): String? {
            return value?.toString() ?: "None"
        }

        fun setEnv(mode: Int, c: Context) {
            saveInt(ENV, mode, c)
        }

        fun chooseEnv(pos: Int, c: Context) {
            when (pos) {
                R.id.mode_normal -> setEnv(PROD_ENV, c)
                R.id.mode_debug -> setEnv(DEBUG_ENV, c)
                R.id.mode_noSim -> setEnv(TEST_ENV, c)
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