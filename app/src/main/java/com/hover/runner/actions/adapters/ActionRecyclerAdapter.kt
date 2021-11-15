package com.hover.runner.actions.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.utils.UIHelper
import com.hover.runner.R
import com.hover.runner.actions.listeners.ActionClickListener
import com.hover.runner.actions.models.Action
import com.hover.runner.utils.RunnerColor

class ActionRecyclerAdapter(private val actionList: List<Action>, private val clickListener: ActionClickListener) :
    RecyclerView.Adapter<ActionRecyclerAdapter.ActionListItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.action_list_items, parent, false)
        return ActionListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActionListItemViewHolder, position: Int) {
        val action: Action = actionList[position]

        UIHelper.underlineText(holder.actionIdText, action.id)
        holder.actionIdText.setTextColor(RunnerColor(holder.itemView.context).get(action.getStatusColor()))
        holder.actionTitleText.text = action.title

        holder.iconImage.setImageResource(action.getStatusDrawable())

        holder.itemView.setOnClickListener { view->
            action.id.let { clickListener.onActionItemClick(action.id, view)}
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return actionList.size
    }

    class ActionListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var actionIdText: TextView = itemView.findViewById(R.id.actionIdText_Id)
        var actionTitleText: TextView = itemView.findViewById(R.id.actionTitle_Id)
        var iconImage: ImageView = itemView.findViewById(R.id.actionIconStatus)
    }
}