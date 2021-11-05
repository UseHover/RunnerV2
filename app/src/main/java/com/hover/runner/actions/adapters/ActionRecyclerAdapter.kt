package com.hover.runner.actions.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.utils.UIHelper
import com.hover.runner.R
import com.hover.runner.actions.ActionClickListener
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
        setIdTextColor(holder.actionIdText, action)
        holder.actionTitleText.text = action.title

        if (action.isNotYetRun()) holder.iconImage.setImageResource(UIHelper.getActionIconDrawable(action.statusEnum))

        holder.itemView.setOnClickListener {
            action.id?.let { clickListener.onClick(action.id!!)}
        }
    }

    private fun setIdTextColor(actionIdView: TextView, action: Action) {
        with(actionIdView) {
            when {
                action.isSuccessful() -> setTextColor(RunnerColor(context).GREEN)
                action.isPending() -> setTextColor(RunnerColor(context).YELLOW)
                action.isFailed() -> setTextColor(RunnerColor(context).RED)
            }
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