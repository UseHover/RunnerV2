package com.hover.runner.filter_actions.model

import kotlin.random.Random

data class CheckBoxItem(val title : String, var isChecked : Boolean, var subTitle : String = "") {
    val id : String = title.replace(" ","") + Random(99999).nextInt().toString()

    companion object {
        fun toList(allTitles: List<String>, titlesInFilter : List<String>) : List<CheckBoxItem> {
            val checkBoxItems  = mutableListOf<CheckBoxItem>()
            allTitles.forEach {
                val isChecked = titlesInFilter.contains(it)
                checkBoxItems.add(CheckBoxItem(it, isChecked))
            }
            return checkBoxItems
        }
    }
}