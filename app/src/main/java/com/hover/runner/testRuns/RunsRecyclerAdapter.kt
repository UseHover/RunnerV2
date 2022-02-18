package com.hover.runner.testRuns

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.utils.DateUtils

class RunsRecyclerAdapter(private val items: List<TestRun>, private val clickListener: TestRunClickListener) :
		RecyclerView.Adapter<RunsRecyclerAdapter.RunsItemViewHolder>() {

	inner class RunsItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var title: TextView = itemView.findViewById(R.id.title)
		var subtitle: TextView = itemView.findViewById(R.id.subtitle)
		var icon: ImageView = itemView.findViewById(R.id.icon)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunsItemViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
		return RunsItemViewHolder(view)
	}

	override fun onBindViewHolder(holder: RunsItemViewHolder, position: Int) {
		val run: TestRun = items[position]

		holder.title.text  = run.name
		//		holder.actionIdText.setTextColor(RunnerColor(holder.itemView.context).get(action.getStatusColor()))
		if (run.frequency == ONCE)
			holder.subtitle.text = DateUtils.humanFriendlyDateTime(run.start_at)
		else
			holder.subtitle.text = holder.itemView.context.resources.getStringArray(R.array.frequency_array)[run.frequency]

		holder.itemView.setOnClickListener { view ->
			clickListener.onItemClick(run.id, view)
		}
	}

	override fun getItemId(position: Int): Long { return position.toLong() }

	override fun getItemViewType(position: Int): Int { return position }

	override fun getItemCount(): Int { return items.size }

	interface TestRunClickListener {
		fun onItemClick(testRunId: Long, titleTextView: View)
	}
}