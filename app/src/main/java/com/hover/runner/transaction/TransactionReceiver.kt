package com.hover.runner.transaction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hover.runner.transaction.repo.TransactionRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionReceiver : BroadcastReceiver(), KoinComponent {
    private val repo: TransactionRepo by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            CoroutineScope(Dispatchers.IO).launch {
                if (context != null) repo.insertOrUpdateTransaction(
                    intent,
                    context.applicationContext
                )
            }
        }
    }
}