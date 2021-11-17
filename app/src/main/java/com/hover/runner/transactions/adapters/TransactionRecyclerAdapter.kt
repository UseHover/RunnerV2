package com.hover.runner.transactions.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.transactions.listeners.TransactionClickListener
import com.hover.runner.transactions.model.RunnerTransaction
import com.hover.runner.utils.RunnerColor


class TransactionRecyclerAdapter(
    private val transactionList: List<RunnerTransaction>,
    private val clickListener: TransactionClickListener
) :
    RecyclerView.Adapter<TransactionRecyclerAdapter.TransactionListItemView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListItemView {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_list_items, parent, false)
        return TransactionListItemView(view)
    }

    override fun onBindViewHolder(holder: TransactionListItemView, position: Int) {
        val transaction = transactionList[position]
        holder.date.text = transaction.getDate()
        holder.content.text = transaction.last_message_hit
        holder.date.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            transaction.getStatusDrawable(),
            0
        )
        holder.date.compoundDrawablePadding = 8
        holder.date.setTextColor(RunnerColor(holder.itemView.context).get(transaction.getStatusColor()))
        holder.itemView.setOnClickListener { clickListener.onTransactionItemClicked(transaction.uuid) }
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

    class TransactionListItemView(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var date: TextView = itemView.findViewById(R.id.transaction_date_id)
        var content: TextView = itemView.findViewById(R.id.transaction_content_id)
    }
}