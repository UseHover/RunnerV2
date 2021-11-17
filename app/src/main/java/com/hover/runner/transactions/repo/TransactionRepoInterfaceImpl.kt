package com.hover.runner.transactions.repo

import android.content.Context
import android.os.Build
import androidx.lifecycle.LiveData
import com.google.android.gms.common.util.ArrayUtils
import com.hover.runner.ApplicationInstance
import com.hover.runner.BuildConfig
import com.hover.runner.actions.models.Action
import com.hover.runner.actions.repo.ActionRepo
import com.hover.runner.parser.model.Parser
import com.hover.runner.settings.fragment.SettingsFragment
import com.hover.runner.transactions.model.RunnerTransaction
import com.hover.runner.transactions.model.TransactionDetailsInfo
import com.hover.runner.utils.DateUtils
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.Utils
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import com.hover.sdk.sms.MessageLog
import com.hover.sdk.transactions.Transaction
import org.json.JSONArray
import org.json.JSONException
import java.lang.Exception
import java.util.ArrayList

class TransactionRepoInterfaceImpl(private val repo: TransactionRepo,
                                   private val actionRepo: ActionRepo,
                                   private val context: Context ) : TransactionRepoInterface {
    override suspend fun getAllTransactions(): List<RunnerTransaction> {
        return repo.getAllTransactions()
    }
    override suspend fun getTransactionsByAction(actionId: String): List<RunnerTransaction> {
        return repo.getTransactionsByAction(actionId)
    }

    override fun getTransaction(uuid: String): LiveData<RunnerTransaction> {
        return repo.getTransaction(uuid)
    }

    override suspend fun getTransactionSuspended(uuid: String): RunnerTransaction? {
        return repo.getTransactionSuspended(uuid)
    }

    override fun getTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>> {
        return repo.getTransactionsByAction(actionId, limit)
    }

    override suspend fun getLastTransaction(actionId: String): RunnerTransaction? {
        return repo.getLastTransaction(actionId)
    }

    override suspend fun getAction(actionId: String): Action {
        val hoverAction = actionRepo.getHoverAction(actionId)
        val lastTransaction = repo.getLastTransaction(actionId)
        return Action.get(hoverAction, lastTransaction, context)
    }

    override suspend fun getDeviceId(): String {
        return Utils.getDeviceId(context) ?: ""
    }

    override suspend fun getHoverTransaction(uuid: String): Transaction {
        return Hover.getTransaction(uuid, context)
    }

    override suspend fun getMessageLog(smsUUID: String) : MessageLog {
        return Hover.getSMSMessageByUUID(smsUUID, context)
    }


}