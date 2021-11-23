package com.hover.runner.transaction.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.transaction.adapters.TransactionMessagesRecyclerAdapter.TransactionMessageViewHolder
import com.hover.runner.transaction.model.TransactionDetailsMessages

class TransactionMessagesRecyclerAdapter(private val messagesModelArrayList: List<TransactionDetailsMessages>) :
    RecyclerView.Adapter<TransactionMessageViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionMessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_messages_items, parent, false)
        return TransactionMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionMessageViewHolder, position: Int) {
        val (enteredValue, messageContent) = messagesModelArrayList[position]
        if (enteredValue!!.isNotEmpty()) {
            if (enteredValue == "(pin)") {
                holder.enteredValueText.text = "...."
                holder.enteredValueText.textSize = 60f
            } else holder.enteredValueText.text = enteredValue
        } else holder.enteredValueText.visibility = View.GONE

        holder.messageContentText.text = messageContent
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return messagesModelArrayList.size
    }

    class TransactionMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var enteredValueText: TextView = itemView.findViewById(R.id.message_enteredValue)
        var messageContentText: TextView = itemView.findViewById(R.id.message_content)
    }
}