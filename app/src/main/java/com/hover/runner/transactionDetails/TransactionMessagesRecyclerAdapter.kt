package com.hover.runner.transactionDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.transactionDetails.TransactionMessagesRecyclerAdapter.TransactionMessageViewHolder

class TransactionMessagesRecyclerAdapter(private val messagePairs: List<TransactionMessages>) :
	RecyclerView.Adapter<TransactionMessageViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionMessageViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.transaction_messages_items, parent, false)
		return TransactionMessageViewHolder(view)
	}

	override fun onBindViewHolder(holder: TransactionMessageViewHolder, position: Int) {
		val model: TransactionMessages = messagePairs[position]

		if (model.enteredValue.isNotEmpty()) holder.enteredValueText.text = model.enteredValue
		else holder.enteredValueText.visibility = View.GONE

		if (!model.responseMessage.isEmpty()) holder.messageContentText.text = model.responseMessage
		else holder.messageContentText.visibility = View.GONE
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getItemViewType(position: Int): Int {
		return position
	}

	override fun getItemCount(): Int {
		return messagePairs.size
	}

	class TransactionMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var enteredValueText: TextView = itemView.findViewById(R.id.message_enteredValue)
		var messageContentText: TextView = itemView.findViewById(R.id.message_content)
	}
}