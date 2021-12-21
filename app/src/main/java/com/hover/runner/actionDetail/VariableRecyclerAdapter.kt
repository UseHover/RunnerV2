package com.hover.runner.actionDetail

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hover.runner.R
import com.hover.runner.utils.SharedPrefUtils


class VariableRecyclerAdapter(private val variables: List<String>) : RecyclerView.Adapter<VariableRecyclerAdapter.VariableItemListView>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariableItemListView {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.variable_item, parent, false)
		return VariableItemListView(view)
	}

	private fun getVariableValue(name: String, context: Context) : String? {
		return SharedPrefUtils.getVarValue(name, context)
	}

	override fun onBindViewHolder(holder: VariableItemListView, position: Int) {
		val key: String = variables[position]
		val value: String? = getVariableValue(key, holder.view.context)
		holder.input.hint = key
		holder.edittext.setText(value)

		holder.edittext.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable) {
				SharedPrefUtils.saveVar(key, s.toString(), holder.view.context)
			}
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
		})
	}

	override fun getItemId(position: Int): Long { return position.toLong() }

	override fun getItemViewType(position: Int): Int { return position }

	override fun getItemCount(): Int { return variables.size }

	class VariableItemListView(val view: View) : RecyclerView.ViewHolder(view) {
		val input: TextInputLayout = itemView.findViewById(R.id.inputLayout)
		val edittext: TextInputEditText = itemView.findViewById(R.id.inputEditText)
	}

}

