package com.hover.runner.database

import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONException

class Converters {
	@TypeConverter
	fun toArr(value: String): List<String> {
		return value.split(",")
	}

	@TypeConverter
	fun fromArr(stringList: List<String>): String {
		return stringList.joinToString(",")
	}
}