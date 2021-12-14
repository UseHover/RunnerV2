package com.hover.runner.actions.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.utils.SharedPrefUtils
import com.hover.sdk.actions.HoverAction


internal class VariableRecyclerAdapter(private val action: HoverAction) : RecyclerView.Adapter<VariableRecyclerAdapter.VariableItemListView>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariableItemListView {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.variables_items, parent, false)
		return VariableItemListView(view)
	}

	private fun getVariableValue(name: String?, context: Context) : String? {
		return SharedPrefUtils.getSavedString("variable-$name", context)
	}

	override fun onBindViewHolder(holder: VariableItemListView, position: Int) {
		action.requiredParams.get(position)
		val key: String? = action.requiredParams.get(position)
		val value: String? = getVariableValue(key, holder.view.context)
		holder.view.tag = action.public_id + key
		holder.labelText.text = key
		holder.editText.setText(value)

		holder.editText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable) {
				SharedPrefUtils.saveString("variable-$key", s.toString(), holder.view.context)
			} //No changes needed, but required to implement the method
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
		})
	}

	override fun getItemId(position: Int): Long { return position.toLong() }

	override fun getItemViewType(position: Int): Int { return position }

	override fun getItemCount(): Int {
		return action.requiredParams.size
	}

	class VariableItemListView(val view: View) : RecyclerView.ViewHolder(view) {
		val labelText: TextView = itemView.findViewById(R.id.variable_label_id)
		val editText: EditText = itemView.findViewById(R.id.variableEditId)
	}

}

