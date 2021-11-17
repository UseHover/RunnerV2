package com.hover.runner.action.models

import android.content.Context
import com.google.gson.Gson
import com.hover.runner.utils.SharedPrefUtils
import java.util.*

data class ActionVariablesCache(val actionMap: Map<String, String>) {
    fun serialize(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        const val THROTTLE: Long = 800
        private fun init(serializedData: String?): ActionVariablesCache? {
            val gson = Gson()

            return if (serializedData != null && serializedData.length > 1)
                gson.fromJson(serializedData, ActionVariablesCache::class.java)
            else
                null

        }

        fun save(actionId: String, label: String, value: String, c: Context) {
            val map = HashMap<String, String>()
            map[label] = value.trim { it <= ' ' }
            SharedPrefUtils.saveString(actionId, ActionVariablesCache(map).serialize(), c)
        }

        fun get(c: Context, actionId: String): ActionVariablesCache {
            return init(SharedPrefUtils.getSavedString(actionId, c))
                ?: ActionVariablesCache(HashMap<String, String>())
        }
    }
}