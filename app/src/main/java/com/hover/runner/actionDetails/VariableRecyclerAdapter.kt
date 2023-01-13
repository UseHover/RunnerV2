package com.hover.runner.actionDetails

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
import com.hover.sdk.actions.HoverAction


class VariableRecyclerAdapter(private val action: HoverAction) : RecyclerView.Adapter<VariableRecyclerAdapter.VariableItemListView>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariableItemListView {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.variable_item, parent, false)
		return VariableItemListView(view)
	}

	private fun getVariableValue(name: String?, context: Context) : String {
		return SharedPrefUtils.getVarValue(action.public_id, name, context)
	}

	override fun onBindViewHolder(holder: VariableItemListView, position: Int) {
		val key: String? = getKeyForPos(position)
		val value: String? = getVariableValue(key, holder.view.context)
		holder.input.hint = key
		holder.edittext.setText(value)

		holder.edittext.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable) {
				SharedPrefUtils.saveVar(action.public_id, key, s.toString(), holder.view.context)
			}
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
		})
	}

	private fun getKeyForPos(position: Int): String? {
		val keys: Iterator<String> = action.required_params.keys()
		var idx = 0
		while (keys.hasNext()) {
			val key = keys.next()
			if (position == idx) { return key }
			idx += 1
		}
		return null
	}

	override fun getItemId(position: Int): Long { return position.toLong() }

	override fun getItemViewType(position: Int): Int { return position }

	override fun getItemCount(): Int { return action.required_params.length() }

	class VariableItemListView(val view: View) : RecyclerView.ViewHolder(view) {
		val input: TextInputLayout = itemView.findViewById(R.id.inputLayout)
		val edittext: TextInputEditText = itemView.findViewById(R.id.inputEditText)
	}

}

