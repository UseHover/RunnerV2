package com.hover.runner.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hover.runner.R

class UIHelper {
	companion object {
		private val INITIAL_ITEMS_FETCH = 30

		fun RecyclerView.setLayoutManagerToLinear() {
			val linearLayoutManager = LinearLayoutManager(this.context)
			linearLayoutManager.initialPrefetchItemCount = INITIAL_ITEMS_FETCH
			linearLayoutManager.isSmoothScrollbarEnabled = true
			this.layoutManager = linearLayoutManager
		}

		fun changeStatusBarColor(activity: Activity, color: Int) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
			val window = activity.window
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//				window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
			window.statusBarColor = color
		}

		fun getStatusColor(status: String, context: Context): Int {
			return when(status) {
				"failed" -> context.resources.getColor(R.color.runnerRed)
				"pending" -> context.resources.getColor(R.color.runnerYellow)
				"succeeded" -> context.resources.getColor(R.color.runnerGreen)
				else -> context.resources.getColor(R.color.runnerDark)
			}
		}

		fun flashMessage(context: Context, view: View?, message: String?) {
			if (view == null) flashMessage(context, message)
			else Snackbar.make(view, message!!, Snackbar.LENGTH_LONG).show()
		}

		fun flashMessage(context: Context, message: String?) {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
		}
	}

}