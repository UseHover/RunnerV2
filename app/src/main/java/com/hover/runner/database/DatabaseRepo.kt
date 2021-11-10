package com.hover.runner.database

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.hover.runner.actions.models.ActionDetails
import com.hover.runner.actions.models.Action
import com.hover.runner.actions.usecase.ActionRepoInterface
import com.hover.runner.transactions.RunnerTransaction
import com.hover.runner.transactions.RunnerTransactionDao
import com.hover.runner.transactions.usecase.TransactionRepoInterface
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import com.hover.sdk.database.HoverRoomDatabase
import com.hover.sdk.parsers.HoverParser
import com.hover.sdk.transactions.TransactionContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DatabaseRepo(db: AppDatabase, private val sdkDB: HoverRoomDatabase, private val context: Context) : DatabaseRepoInterface {

    private val transactionDao: RunnerTransactionDao = db.runnerTransactionDao()

    override suspend fun getAllActionsFromHover(): List<HoverAction> {
       return sdkDB.actionDao().all
    }

    override suspend fun getHoverAction(id: String): HoverAction {
        return sdkDB.actionDao().getAction(id)
    }

    override fun getAllTransactions(): LiveData<List<RunnerTransaction>> {
        return transactionDao.allTransactions
    }

    override fun getTransaction(uuid: String): LiveData<RunnerTransaction> {
        return transactionDao.getTransaction(uuid)
    }

    override suspend fun getTransactionSuspended(uuid: String): RunnerTransaction? {
        return transactionDao.getTransaction_Suspended(uuid)
    }

    override fun getTransactionsByAction(actionId: String): LiveData<List<RunnerTransaction>> {
        return transactionDao.transactionsByAction(actionId)
    }

    override fun getTransactionsByAction(actionId: String, limit: Int): LiveData<List<RunnerTransaction>> {
        return transactionDao.transactionsByAction(actionId, limit)
    }

    override suspend fun getLastTransaction(actionId: String): RunnerTransaction? {
        return transactionDao.lastTransactionsByAction_Suspended(actionId)
    }

    override suspend fun getTransactionsByActionSuspended(actionId: String): List<RunnerTransaction> {
        return transactionDao.transactionsByAction_Suspended(actionId)
    }

    override fun updateTransaction(transaction: RunnerTransaction) {
        transactionDao.update(transaction)
    }

    override fun insertTransaction(transaction: RunnerTransaction) {
        transactionDao.insert(transaction)
    }

    override fun insertOrUpdateTransaction(intent: Intent, context: Context) {
        AppDatabase.databaseWriteExecutor.execute {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var t : RunnerTransaction? = intent.getStringExtra(TransactionContract.COLUMN_UUID)?.let { getTransactionSuspended(it) }
                    if (t == null) {
                        t = RunnerTransaction.init(intent, context)
                        t?.let{ insertTransaction(t!!)}
                        t = getTransactionSuspended(t!!.uuid)
                    } else {
                        t.update(intent)
                        updateTransaction(t)
                    }
                    if (t != null) {
                        Timber.e("save t with uuid: %s", t.uuid)
                    };
                } catch (e: Exception) {
                    Timber.e(e, "error")
                }
            }
        }
    }

    override suspend fun getParsersByActionId(actionId: String?): List<HoverParser> {
        return HoverParser.loadUSSDForAction(actionId, context)
    }

    override suspend fun getParser(id: Int): HoverParser? {
        return HoverParser.load(IntArray(id), context).first()
    }


}