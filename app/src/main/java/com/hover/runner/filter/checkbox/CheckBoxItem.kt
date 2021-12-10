package com.hover.runner.filter.checkbox

import com.hover.runner.action.models.Action
import kotlin.random.Random

data class CheckBoxItem(val title: String, var isChecked: Boolean, var subTitle: String = "") {
	val id: String = title.replace(" ", "") + Random(99999).nextInt().toString()

	companion object {
		fun toList(allTitles: List<String>, titlesInFilter: List<String>): List<CheckBoxItem> {
			val checkBoxItems = mutableListOf<CheckBoxItem>()
			allTitles.forEach {
				val isChecked = titlesInFilter.contains(it)
				checkBoxItems.add(CheckBoxItem(it, isChecked))
			}
			return checkBoxItems
		}

		fun toListByActions(allActions: List<Action>,
		                    actionsIdInFilter: List<String>): List<CheckBoxItem> {
			val checkBoxItems = mutableListOf<CheckBoxItem>()
			allActions.forEach {
				val isChecked = actionsIdInFilter.contains(it.id)
				checkBoxItems.add(CheckBoxItem(it.title, isChecked, it.id))
			}
			return checkBoxItems
		}
	}
}