package com.hover.runner.actions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.utils.StatusUiTranslator
import com.hover.runner.utils.TextViewUtils.Companion.underline
import com.hover.sdk.actions.HoverAction

internal class ActionRecyclerAdapter(private val actionList: List<HoverAction>, private val statuses: HashMap<String, String?>, private val clickListener: ActionClickListener) :
	RecyclerView.Adapter<ActionRecyclerAdapter.ActionListItemViewHolder>(), StatusUiTranslator {

	inner class ActionListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var actionIdText: TextView = itemView.findViewById(R.id.title)
		var actionTitleText: TextView = itemView.findViewById(R.id.subtitle)
		var iconImage: ImageView = itemView.findViewById(R.id.icon)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionListItemViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
		return ActionListItemViewHolder(view)
	}

	override fun onBindViewHolder(holder: ActionListItemViewHolder, position: Int) {
		val action: HoverAction = actionList[position]

		holder.actionIdText.underline(action.public_id)
		holder.actionTitleText.text = action.name

		if (statuses.containsKey(action.public_id)) {
			holder.actionIdText.setTextColor(getHeaderColor(statuses[action.public_id], holder.itemView.context))
			holder.iconImage.setImageResource(getStatusIcon(statuses[action.public_id]))
			holder.iconImage.visibility = View.VISIBLE
		}

		holder.itemView.setOnClickListener { view ->
			action.public_id.let { clickListener.onActionItemClick(it, view) }
		}
	}

	override fun getItemId(position: Int): Long { return position.toLong() }

	override fun getItemViewType(position: Int): Int { return position }

	override fun getItemCount(): Int { return actionList.size }

	interface ActionClickListener {
		fun onActionItemClick(actionId: String, titleTextView: View)
	}
}