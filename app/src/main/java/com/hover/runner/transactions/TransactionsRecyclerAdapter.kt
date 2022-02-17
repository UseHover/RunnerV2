package com.hover.runner.transactions


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.utils.DateUtils
import com.hover.sdk.transactions.Transaction


class TransactionsRecyclerAdapter(private val transactionList: List<Transaction>, private val clickListener: TransactionClickListener) :
	RecyclerView.Adapter<TransactionsRecyclerAdapter.TransactionListItemView>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListItemView {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_items, parent, false)
		return TransactionListItemView(view)
	}

	override fun onBindViewHolder(holder: TransactionListItemView, position: Int) {
		val transaction = transactionList[position]
		holder.date.text = DateUtils.timestampTemplate(transaction.reqTimestamp)
		holder.content.text = if (transaction.userMessage.isNullOrEmpty()) transaction.category else transaction.userMessage
//		holder.date.setTextColor(RunnerColor(holder.itemView.context).get(transaction.getStatusColor()))
		holder.itemView.setOnClickListener { clickListener.onItemClick(transaction.uuid) }
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getItemViewType(position: Int): Int {
		return position
	}

	override fun getItemCount(): Int {
		return transactionList.size
	}

	class TransactionListItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var date: TextView = itemView.findViewById(R.id.transaction_date_id)
		var content: TextView = itemView.findViewById(R.id.transaction_content_id)
	}

	interface TransactionClickListener {
		fun onItemClick(uuid: String)
	}
}