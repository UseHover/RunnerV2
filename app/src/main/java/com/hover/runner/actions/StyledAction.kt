package com.hover.runner.actions

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.hover.runner.BuildConfig
import com.hover.runner.transaction.StatusUIHelper
import com.hover.runner.transaction.model.RunnerTransaction
import com.hover.runner.utils.SharedPrefUtils
import com.hover.sdk.actions.HoverAction
import org.json.JSONArray

class StyledAction(val action: HoverAction) : HoverAction(), StatusUIHelper {

	var steps: JSONArray? = null


	fun getStatusColor(): Int {
		return getColor(status)
	}

	fun getLayoutColor(): Int {
		return getToolBarColor(status)
	}

	fun getStatusDrawable(): Int {
		return getDrawable(status)
	}
}

