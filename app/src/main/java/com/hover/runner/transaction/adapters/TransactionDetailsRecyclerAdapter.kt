package com.hover.runner.transaction.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.action.listeners.ActionClickListener
import com.hover.runner.parser.listeners.ParserClickListener
import com.hover.runner.transaction.TransactionStatus
import com.hover.runner.transaction.model.TransactionDetailsInfo
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.TextViewUtils.Companion.underline


class TransactionDetailsRecyclerAdapter(
    private val infoList: List<TransactionDetailsInfo>,
    private val actionClickListener: ActionClickListener,
    private val parserClickListener: ParserClickListener,
    private var hasActionId: Boolean = false
) : RecyclerView.Adapter<TransactionDetailsRecyclerAdapter.TDViewHolder>() {

    private lateinit var actionId: String
    private lateinit var actionName: String

    init {
        if (hasActionId) {
            infoList.forEach {
                if (it.label == "ActionID") actionId = it.value
                if (it.label == "Action") actionName = it.value
            }
        }
    }
    fun getActionId() :String{
        return actionId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TDViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.transac_details_list_items, parent, false)
        return TDViewHolder(view)
    }

    override fun onBindViewHolder(holder: TDViewHolder, position: Int) {
        val info: TransactionDetailsInfo = infoList[position]
        holder.label.text = info.label
        holder.value.text = info.value

        if (info.label == "Status") {
            holder.value.setTextColor(
                RunnerColor(holder.itemView.context).get(
                    TransactionStatus.getColor(
                        info.value
                    )
                )
            )
        }

        if (info.clickable) {
            holder.value.underline()
            if (info.label.contains("Action")) holder.value.setOnClickListener {
                actionClickListener.onActionItemClick(
                    actionId,
                    holder.value
                )
            }
            else holder.value.setOnClickListener { parserClickListener.onParserItemClicked(info.value) }
        }
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class TDViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var label: TextView = itemView.findViewById(R.id.transac_det_label)
        var value: TextView = itemView.findViewById(R.id.transac_det_value)
    }
}
