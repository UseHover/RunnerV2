package com.hover.runner.action.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hover.runner.utils.Utils
import com.hover.sdk.api.Hover
import timber.log.Timber

internal class UpdateHoverActions(private val listener: Hover.DownloadListener, private val context: Context) {

        fun init() {
            LocalBroadcastManager.getInstance(context).registerReceiver(
                actionUpdateReceiver,
                IntentFilter(Utils.getPackage(context).toString() + ".ACTIONS_DOWNLOADED")
            )
            Hover.updateActionConfigs(listener, context)
        }

        private var actionUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                unregisterReceiver()
            }
        }

        private fun unregisterReceiver() {
            try { LocalBroadcastManager.getInstance(context).unregisterReceiver(actionUpdateReceiver) }
            catch (error: Exception) { Timber.e(error) }
        }
}