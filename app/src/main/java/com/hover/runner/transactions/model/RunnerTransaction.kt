package com.hover.runner.transactions.model

import android.content.Context
import android.content.Intent
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hover.runner.transactions.TransactionStatus
import com.hover.runner.utils.DateUtils
import com.hover.runner.utils.Utils
import com.hover.sdk.api.Hover
import com.hover.sdk.transactions.Transaction
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber

@Entity(tableName = "runner_transactions", indices = [Index(value = ["uuid"], unique = true)])
data class RunnerTransaction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?,

    @ColumnInfo(name = "uuid")
    var uuid: String?,

    @ColumnInfo(name = "action_id")
    var action_id: String?,

    @ColumnInfo(name = "environment", defaultValue = "0")
    var environment: Int?,

    @ColumnInfo(name = "status", defaultValue = Transaction.PENDING)
    var status: String?,

    @ColumnInfo(name = "category")
    var category: String?,

    @ColumnInfo(name = "initiated_at", defaultValue = "CURRENT_TIMESTAMP")
    var initiated_at: Long?,

    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    var updated_at: Long,

    @ColumnInfo(name = "last_message_hit")
    var last_message_hit: String?,

    @ColumnInfo(name = "matched_parsers")
    var matched_parsers : String?
    ) : TransactionStatus() {
    fun update(data: Intent) {
        status = data.getStringExtra(column_status)!!
    }

    fun getDate() : String? {
        return DateUtils.formatDate(updated_at!!)
    }

    fun getStatusDrawable() : Int {
        return getDrawable(status)
    }

    fun getStatusColor() : Int{
        return getColor(status)
    }

    companion object {
        private const val uuid = "transaction_uuid";
        private const val action_id = "action_id"
        private const val environment = "environment"
        private const val column_status = "status"
        private const val column_category = "category"
        private const val column_request_timestamp = "request_timestamp"
        private const val column_update_timestamp = "update_timestamp"
        private const val column_matched_parsers = "matched_parsers"
        fun init(data: Intent, context: Context) : RunnerTransaction? {

            return if (data.hasExtra(uuid) && data.getStringExtra(uuid) != null) {
                val uuid = data.getStringExtra(uuid)!!
                val action_id = data.getStringExtra(action_id)!!
                val environment = data.getIntExtra(environment, 0)
                val status = data.getStringExtra(column_status)!!
                val category = data.getStringExtra(column_category)
                val initiated_at = data.getLongExtra(column_request_timestamp, DateUtils.now())
                val updated_at = data.getLongExtra(column_update_timestamp, initiated_at)
                val matched_parsers = data.getStringExtra(column_matched_parsers)
                val last_message_hit = getLastMessageHit(Hover.getTransaction(uuid, context), context)
                Timber.v("creating transaction with uuid: %s", uuid)
                RunnerTransaction(-1, uuid,action_id, environment,status, category, initiated_at, updated_at, last_message_hit, matched_parsers)
            } else null
        }


        private fun getLastMessageHit(transaction : Transaction, context: Context)  : String? {
            var lastUSSDMessage: String? = "empty"
            val smsMessage: String? = lastSMSMessage(transaction.smsHits, context);
            if (smsMessage != null) {
                lastUSSDMessage = smsMessage
            } else {
                try {
                    lastUSSDMessage = transaction.ussdMessages.getString(transaction.ussdMessages.length() - 1)
                } catch (ignored: JSONException) {
                }
            }
            return lastUSSDMessage
        }


        private fun lastSMSMessage(smsHits: JSONArray, context: Context): String? {
            var smsMessage: String? = null
            try {
                val smsUUIDS = Utils.convertNormalJSONArrayToStringArray(smsHits)
                if (smsUUIDS!!.isNotEmpty()) {
                    val lastUUID = smsUUIDS[smsUUIDS.size - 1]
                    smsMessage = Hover.getSMSMessageByUUID(lastUUID, context).msg
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return smsMessage
        }
    }

}
