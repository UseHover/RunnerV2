package com.hover.runner.database

import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONException

public class Converters {
	@TypeConverter
	fun toJsonArr(value: String?): JSONArray? {
		return try {
			value?.let { JSONArray(it) }
		} catch (e: JSONException) {
			null
		}
	}

	@TypeConverter
	fun fromJsonArr(arr: JSONArray?): String? {
		return arr?.toString()
	}
}