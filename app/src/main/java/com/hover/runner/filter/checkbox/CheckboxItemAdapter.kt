package com.hover.runner.filter.checkbox

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import java.util.*

internal class CheckboxItemAdapter(private val checkBoxItems: List<CheckBoxItem>,
                                   private val checkBoxStatus: CheckBoxListStatus,
                                   private val isActive: Boolean = true) :
	ListAdapter<CheckBoxItem, CheckboxItemAdapter.CheckBoxViewHolder>(CheckBoxItemDiffCallback()) {

	private val checkedTitles = mutableListOf<String>()

	fun getCheckedItemTitles(): List<String> {
		return checkedTitles
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckBoxViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.checkbox_item_layout, parent, false)
		return CheckBoxViewHolder(view)
	}

	override fun onBindViewHolder(holder: CheckBoxViewHolder, position: Int) {
		val item = checkBoxItems[position]

		holder.checkBox.isChecked = item.isChecked
		setTitle(holder.checkBox, item.title)
		setSubTitle(holder.subtitleTextView, item.subTitle)


		if (isActive) holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
			checkBoxStatus.anItemSelected()
			if(isChecked) checkedTitles.add(item.title)
			else checkedTitles.remove(item.title)
		 }
		else setItemInActive(holder)

	}

	private fun setItemInActive(holder: CheckBoxViewHolder) {
		holder.checkBox.setTint(Color.GRAY)
		holder.checkBox.setTextColor(Color.GRAY)
		holder.checkBox.isClickable = false
	}

	private fun AppCompatCheckBox.setTint(tintColor: Int) {
		if (Build.VERSION.SDK_INT < 21) CompoundButtonCompat.setButtonTintList(this,
		                                                                       ColorStateList.valueOf(
			                                                                       tintColor))
		else this.buttonTintList = ColorStateList.valueOf(tintColor)
	}

	private fun setTitle(checkbox: AppCompatCheckBox, value: String) {
		fun isCountryCode() = value.length == 2
		if (isCountryCode()) {
			checkbox.isAllCaps = true
			checkbox.text = Locale("EN", value).displayCountry
		}
		else checkbox.text = value
	}

	private fun setSubTitle(textView: TextView, value: String) {
		if (value.isNotEmpty()) {
			textView.visibility = View.VISIBLE
			textView.text = value
		}
	}

	override fun getItemCount(): Int {
		return checkBoxItems.size
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getItemViewType(position: Int): Int {
		return position
	}

	inner class CheckBoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val checkBox: AppCompatCheckBox = itemView.findViewById(R.id.checkbox_title)
		val subtitleTextView: TextView = itemView.findViewById(R.id.checkbox_subtitle)
	}

	internal interface CheckBoxListStatus {
		fun anItemSelected()
	}
}


private class CheckBoxItemDiffCallback : DiffUtil.ItemCallback<CheckBoxItem>() {
	override fun areItemsTheSame(oldItem: CheckBoxItem, newItem: CheckBoxItem): Boolean {
		return oldItem.id == newItem.id
	}

	override fun areContentsTheSame(oldItem: CheckBoxItem, newItem: CheckBoxItem): Boolean {
		return oldItem == newItem
	}

}
