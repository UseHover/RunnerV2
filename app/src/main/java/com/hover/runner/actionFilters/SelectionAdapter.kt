package com.hover.runner.actionFilters

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R

internal class SelectionAdapter(private val items: List<String>, val selectedItems: MutableList<String>) :
	RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.checkbox_item_layout, parent, false)
		return SelectionViewHolder(view)
	}

	override fun onBindViewHolder(holder: SelectionViewHolder, position: Int) {
		val item = items[position]

		holder.title.text = item
		holder.title.isChecked = selectedItems.contains(item)
		holder.title.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) selectedItems.add(item) else selectedItems.remove(item)
			notifyDataSetChanged()
		}
//		else disableItem(holder)
	}

	private fun AppCompatCheckBox.setTint(tintColor: Int) {
		if (Build.VERSION.SDK_INT < 21)
			CompoundButtonCompat.setButtonTintList(this, ColorStateList.valueOf(tintColor))
		else this.buttonTintList = ColorStateList.valueOf(tintColor)
	}

	private fun disableItem(holder: SelectionViewHolder) {
		holder.title.setTint(Color.GRAY)
		holder.title.setTextColor(Color.GRAY)
		holder.title.isClickable = false
	}

	override fun getItemCount(): Int {
		return items.size
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getItemViewType(position: Int): Int {
		return position
	}

	inner class SelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val title: AppCompatCheckBox = itemView.rootView as AppCompatCheckBox
	}
}