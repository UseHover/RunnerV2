package com.hover.runner.transactions.viewmodel.usecase

import android.os.Build
import androidx.lifecycle.LiveData
import com.google.android.gms.common.util.ArrayUtils
import com.hover.runner.BuildConfig
import com.hover.runner.settings.fragment.SettingsFragment
import com.hover.runner.transactions.model.RunnerTransaction
import com.hover.runner.transactions.model.TransactionDetailsInfo
import com.hover.runner.transactions.model.TransactionDetailsMessages
import com.hover.runner.transactions.repo.TransactionRepoInterface
import com.hover.runner.utils.Utils
import com.hover.sdk.sms.MessageLog
import com.hover.sdk.transactions.Transaction
import org.json.JSONException
import java.util.*

class TransactionUseCaseImpl(private val transactionRepoInterface: TransactionRepoInterface) :
    TransactionUseCase {
    override suspend fun getAllTransactions(): List<RunnerTransaction> {
        return transactionRepoInterface.getAllTransactions()
    }

    override suspend fun getTransactionsByAction(actionId: String): List<RunnerTransaction> {
        return transactionRepoInterface.getTransactionsByAction(actionId)
    }

    override fun getTransaction(uuid: String): LiveData<RunnerTransaction> {
        return transactionRepoInterface.getTransaction(uuid)
    }

    override suspend fun getAboutInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsInfo> {
        with(runnerTransaction) {
            val action = transactionRepoInterface.getAction(runnerTransaction.action_id)
            val lastTransaction: RunnerTransaction? =
                transactionRepoInterface.getLastTransaction(runnerTransaction.action_id)

            val detailsList = mutableListOf<TransactionDetailsInfo>()
            detailsList.add(TransactionDetailsInfo("Status", status, false))
            detailsList.add(TransactionDetailsInfo("Action", action.title!!, true))
            detailsList.add(TransactionDetailsInfo("ActionID", action.id, true))
            detailsList.add(TransactionDetailsInfo("Time", getDate()!!, false))
            detailsList.add(TransactionDetailsInfo("TransactionId", uuid, false))
            detailsList.add(
                TransactionDetailsInfo(
                    "Result",
                    lastTransaction!!.last_message_hit ?: "",
                    false
                )
            )
            detailsList.add(TransactionDetailsInfo("Category", category!!, false))
            detailsList.add(TransactionDetailsInfo("Operator", action.network_name!!, false))
            return detailsList

        }
    }

    override suspend fun getDeviceInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsInfo> {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val osVersionName = Build.VERSION.SDK_INT.toString()
        val deviceId = transactionRepoInterface.getDeviceId()

        with(runnerTransaction) {
            val detailsList = mutableListOf<TransactionDetailsInfo>()
            detailsList.add(
                TransactionDetailsInfo(
                    "Testing mode",
                    SettingsFragment.envToString(environment),
                    false
                )
            )
            detailsList.add(TransactionDetailsInfo("Device ID", deviceId, false))
            detailsList.add(TransactionDetailsInfo("Brand", manufacturer, false))
            detailsList.add(TransactionDetailsInfo("Model", model, false))
            detailsList.add(TransactionDetailsInfo("Android ver.", "SDK $osVersionName", false))
            detailsList.add(
                TransactionDetailsInfo(
                    "App ver. code",
                    BuildConfig.VERSION_CODE.toString(),
                    false
                )
            )
            detailsList.add(
                TransactionDetailsInfo(
                    "App ver. name",
                    BuildConfig.VERSION_NAME,
                    false
                )
            )
            return detailsList
        }
    }

    override suspend fun getDebugInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsInfo> {
        with(runnerTransaction) {
            val hoverTransaction = transactionRepoInterface.getHoverTransaction(uuid)
            val detailsList = mutableListOf<TransactionDetailsInfo>()
            detailsList.add(
                TransactionDetailsInfo(
                    "Input extras",
                    hoverTransaction.input_extras.toString(),
                    false
                )
            )
            detailsList.add(
                TransactionDetailsInfo(
                    "Matched parsers",
                    runnerTransaction.matched_parsers ?: "",
                    true
                )
            )
            detailsList.add(
                TransactionDetailsInfo(
                    "Parsed variables",
                    hoverTransaction.parsed_variables.toString(),
                    false
                )
            )
            return detailsList
        }
    }

    override suspend fun getMessagesInfo(runnerTransaction: RunnerTransaction): List<TransactionDetailsMessages> {
        val action = transactionRepoInterface.getAction(runnerTransaction.action_id)
        val hoverTransaction = transactionRepoInterface.getHoverTransaction(runnerTransaction.uuid)
        val arrayOfStringArray =
            getMessages(action.rootCode, getSMSMessages(hoverTransaction), hoverTransaction)

        return getMessagesModel(arrayOfStringArray)
    }

    override fun getTransactionsByAction(
        actionId: String,
        limit: Int
    ): LiveData<List<RunnerTransaction>> {
        return transactionRepoInterface.getTransactionsByAction(actionId, limit)
    }

    private suspend fun getSMSMessages(transaction: Transaction): List<MessageLog> {
        val smsMessages: MutableList<MessageLog> = ArrayList()
        try {
            val smsUUIDS = Utils.convertNormalJSONArrayToStringArray(transaction.smsHits)
            for (uuid in smsUUIDS!!) {
                val log: MessageLog = transactionRepoInterface.getMessageLog(uuid!!)
                smsMessages.add(log)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return smsMessages
    }

    private fun getMessages(
        rootCodeString: String,
        smsMessages: List<MessageLog>,
        transaction: Transaction
    ): Array<Array<String?>?> {
        val smsSenderList = arrayOfNulls<String>(smsMessages.size)
        val smsContentList = arrayOfNulls<String>(smsMessages.size)
        for (i in smsMessages.indices) {
            smsSenderList[i] = ""
            smsContentList[i] = smsMessages.get(i).msg
        }
        val rootCode = arrayOf(rootCodeString)
        var tempEnteredValues: Array<String?>? = arrayOf()
        try {
            tempEnteredValues = Utils.convertNormalJSONArrayToStringArray(transaction.enteredValues)
        } catch (ignored: JSONException) {
        }

        val aLen = rootCode.size
        val bLen = tempEnteredValues!!.size
        var enteredValues: Array<String?>? = arrayOfNulls(aLen + bLen)
        System.arraycopy(rootCode, 0, enteredValues, 0, aLen)
        System.arraycopy(tempEnteredValues, 0, enteredValues, aLen, bLen)
        enteredValues = ArrayUtils.concat(enteredValues, smsSenderList)

        var ussdMessages: Array<String?>? = arrayOf()
        try {
            ussdMessages = Utils.convertNormalJSONArrayToStringArray(transaction.ussdMessages)
            ussdMessages = ArrayUtils.concat(ussdMessages, smsContentList)
        } catch (ignored: Exception) {
        }
        return arrayOf(enteredValues, ussdMessages)
    }

    private fun getMessagesModel(result: Array<Array<String?>?>): List<TransactionDetailsMessages> {
        val enteredValues = result[0]!!
        val ussdMessages = result[1]!!
        val largestSize = Math.max(enteredValues.size, ussdMessages.size)
        val messagesModels: ArrayList<TransactionDetailsMessages> =
            ArrayList<TransactionDetailsMessages>()

        //Put in a try and catch to prevent crashing when USSD session reports incorrectly.
        try {
            for (i in 0 until largestSize) {
                messagesModels.add(
                    TransactionDetailsMessages(
                        if (enteredValues[i] != null) enteredValues[i] else "",
                        if (ussdMessages[i] != null) ussdMessages[i] else ""
                    )
                )
            }
        } catch (e: Exception) {

            //PUTTING IN ANOTHER TRY AND CATCH TO AVOID ERROR WHEN ON NO-SIM MODE
            try {
                var i = 0
                while (i < largestSize - 1) {
                    messagesModels.add(
                        TransactionDetailsMessages(
                            if (enteredValues[i] != null) enteredValues[i] else "",
                            if (ussdMessages[i] != null) ussdMessages[i] else ""
                        )
                    )
                    i++
                }
            } catch (ex: Exception) {
                //USE THIS FOR NO-SIM MESSAGE MODE;
                messagesModels.add(TransactionDetailsMessages("*ROOT_CODE#", "Test Responses"))
            }
        }
        return messagesModels
    }


}