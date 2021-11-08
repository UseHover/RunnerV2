package com.hover.runner.customViews.detailsTopLayout

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.hover.runner.actions.navigation.ActionNavigationInterface
import com.hover.runner.databinding.DetailsTopLayoutBinding
import com.hover.runner.utils.UIHelper

enum class DetailScreenType {
    ACTION, TRANSACTION
}

class RunnerTopDetailsView(context: Context, attributeSet: AttributeSet) : TopDetailsContentChooser(context, attributeSet),
    RunnerTopDetailsInterface {
    private var binding: DetailsTopLayoutBinding = DetailsTopLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private var titleText: TextView = binding.actionDetailsToolbarText
    private var subtitleText: TextView = binding.actionDetailsSubtoolText
    private var descTitle: TextView = binding.actionDetailsStatusTitle
    private var descContent: TextView = binding.actionDetailsStatusDesc
    private var descLink: TextView = binding.actionDetailsStatusLink
    private var topLayout: LinearLayout = binding.actionDetailsTopLayoutId


    override fun setLayout(status: String, detailsScreenType: DetailScreenType) {
        topLayout.setBackgroundColor(getLayoutBackground(status))
    }

    override fun setTitle(content: String, status : String) {
        titleText.text = content
        titleText.setTextColor(getTitleTextColor(status))
        titleText.setCompoundDrawablesWithIntrinsicBounds(
            getTitleTextCompoundDrawable(status),
            0,
            0,
            0
        )
    }

    override fun setSubTitle(content: String, status : String) {
        subtitleText.text = content
        subtitleText.setTextColor(getSubTitleTextColor(status))
    }
    fun setup(status: String, detailsScreenType: DetailScreenType, activity: Activity) {
        setLayout(status, detailsScreenType)
        setDescription(status, detailsScreenType)
        setDescriptionVisibility(status)
        setViewClicks(status, activity)
    }

    override fun setDescription(status: String, detailsScreenType: DetailScreenType) {
        descTitle.setText(getDescTitle(status, detailsScreenType))
        descTitle.setCompoundDrawablesWithIntrinsicBounds(getDescCompoundDrawable(status), 0, 0, 0)
        descTitle.compoundDrawablePadding = 32

        descContent.setText(getDescContent(status, detailsScreenType))
        UIHelper.underlineText(descLink, resources.getString(getDescLinkLabel(status)))
    }

   override fun setDescriptionVisibility(status: String) {
        descTitle.visibility = getDescVisibility(status)
        descContent.visibility = getDescVisibility(status)
    }

    override fun setViewClicks(status: String, activity: Activity) {
        descLink.setOnClickListener { v: View? ->
           val navInterface = activity as ActionNavigationInterface
            navInterface.navWebView(resources.getString(getWebTitle(status)), resources.getString(getLink()) )
        }

        titleText.setOnClickListener { v: View? -> activity.onBackPressed() }

    }

}