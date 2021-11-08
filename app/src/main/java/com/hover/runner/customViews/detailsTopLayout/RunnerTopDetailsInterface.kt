package com.hover.runner.customViews.detailsTopLayout

import android.app.Activity

internal interface RunnerTopDetailsInterface {
    fun setLayout(status: String, detailsScreenType: DetailScreenType)
    fun setTitle(content: String, status : String)
    fun setSubTitle(content: String, status : String)
    fun setDescription(status: String, detailsScreenType: DetailScreenType)
    fun setDescriptionVisibility(status: String)
    fun setViewClicks(status: String, activity: Activity)
}