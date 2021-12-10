package com.hover.runner.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.hover.runner.R
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber
import java.util.regex.Pattern

class Utils {
	companion object {
		fun validateEmail(string: String): Boolean {
			val patterns =
				Pattern.compile("(?:[A-Za-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
			return patterns.matcher(string).matches()
		}

		fun toString(stringList: List<String>): String {
			var result = ""
			stringList.forEach {
				if(result.isEmpty()) result += it
				else result += ", $it"
			}
			return result
		}

		fun validatePassword(string: String?): Boolean {
			return if (string == null) false
			else string.length in 5..39 && !string.contains(" ")
		}

		fun getPackage(c: Context): String? {
			return try {
				c.applicationContext.packageName
			} catch (e: NullPointerException) {
				"fail"
			}
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

		@SuppressLint("HardwareIds", "MissingPermission")
		fun getDeviceId(c: Context): String? {
			try {
				if (PermissionsUtil.hasPermissions(c,
				                                   arrayOf(Manifest.permission.READ_PHONE_STATE,
				                                           Manifest.permission.CALL_PHONE))) {
					var id: String? = null
					if (Build.VERSION.SDK_INT < 29) {
						try {
							id =
								(c.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
						} catch (e: Exception) { Timber.e(e)}
					}
					if (id == null) {
						id =
							Settings.Secure.getString(c.contentResolver, Settings.Secure.ANDROID_ID)
					}
					return id
				}
			} catch (e: SecurityException) { Timber.e(e)}
			return c.getString(R.string.hsdk_unknown_device_id)
		}
	}
}