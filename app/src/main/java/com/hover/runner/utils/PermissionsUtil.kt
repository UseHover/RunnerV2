package com.hover.runner.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

object PermissionsUtil {
		fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				for (permission in permissions) {
					if (ActivityCompat.checkSelfPermission(context,
					                                       permission) != PackageManager.PERMISSION_GRANTED) {
						return false
					}
				}
			}
			return true
		}
}