package com.hover.runner.customViews.detailsTopLayout

internal interface TopDetailsContentChooseInterface {
    fun getLayoutBackground(status: String) : Int
    fun getTitleTextColor(status: String) : Int
    fun getTitleTextCompoundDrawable(status: String) : Int
    fun getSubTitleTextColor(status: String) : Int
    fun getDescTitle(status: String, detailScreenType: DetailScreenType) : Int
    fun getDescContent(status: String, detailScreenType: DetailScreenType) : Int
    fun getDescCompoundDrawable(status: String) : Int
    fun getDescVisibility(status: String) : Int
    fun getDescLinkLabel(status: String)  : Int
    fun getLink() : Int
    fun getWebTitle(status: String) : Int
}