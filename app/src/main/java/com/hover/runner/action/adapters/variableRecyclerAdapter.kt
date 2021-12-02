package com.hover.runner.action.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.action.listeners.ActionVariableEditListener
import com.hover.runner.action.models.StreamlinedSteps


internal class VariableRecyclerAdapter(
    private val actionId: String,
    private var steps: StreamlinedSteps?,
    private val editInterface: ActionVariableEditListener,
    private var initialData: Map<String, String?>
) : RecyclerView.Adapter<VariableRecyclerAdapter.VariableItemListView>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariableItemListView {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.variables_items, parent, false)
        return VariableItemListView(view)
    }

    override fun onBindViewHolder(holder: VariableItemListView, position: Int) {
        val label: String? = steps?.stepVariableLabel?.get(position)
        val desc: String? = steps?.stepsVariableDesc?.get(position)
        holder.view.tag = actionId + label
        holder.labelText.text = label
        holder.editText.hint = desc

        initialData[label]?.let { if (it.isNotEmpty()) holder.editText.setText(it) }.apply { }

        holder.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                editInterface.updateVariableCache(label!!, s.toString())
            }
        })
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        if (steps == null) return 0
        return if (steps!!.stepVariableLabel.size != steps!!.stepsVariableDesc.size) 0
        else steps!!.stepVariableLabel.size
    }

    class VariableItemListView(val view: View) :
        RecyclerView.ViewHolder(view) {
        val labelText: TextView = itemView.findViewById(R.id.variable_label_id)
        val editText: EditText = itemView.findViewById(R.id.variableEditId)
    }

}

